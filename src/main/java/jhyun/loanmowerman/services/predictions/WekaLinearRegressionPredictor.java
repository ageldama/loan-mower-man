package jhyun.loanmowerman.services.predictions;

import com.google.common.collect.Lists;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.TempFileService;
import jhyun.loanmowerman.services.WekaArffService;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.TrainedPredictionModel;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.storage.repositories.TrainedPredictionModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 해당 은행의 지원금액 내역을 Weka Linear Regression으로 예측
 */
@Slf4j
@Service
public class WekaLinearRegressionPredictor implements Predictor, PredictionPrepper {

    private static final String STRATEGY_NAME = "linear_regression";

    private TempFileService tempFileService;
    private WekaArffService wekaArffService;
    private TrainedPredictionModelRepository trainedPredictionModelRepository;
    private InstituteRepository instituteRepository;

    @Autowired
    public WekaLinearRegressionPredictor(
            TempFileService tempFileService,
            WekaArffService wekaArffService,
            TrainedPredictionModelRepository trainedPredictionModelRepository,
            InstituteRepository instituteRepository
    ) {
        this.tempFileService = tempFileService;
        this.wekaArffService = wekaArffService;
        this.trainedPredictionModelRepository = trainedPredictionModelRepository;
        this.instituteRepository = instituteRepository;
    }

    @Transactional
    @Override
    public void prepare() {
        trainedPredictionModelRepository.deleteByStrategy(STRATEGY_NAME);
        //
        final ArrayList<Institute> banks = Lists.newArrayList(instituteRepository.findAll());
        for (Institute bank : banks) {
            // prepare temp file (.arff)
            File tempFile;
            try {
                tempFile = tempFileService.createTempFile(".arff");
            } catch (IOException e) {
                log.error("create-temp-file fail", e);
                break;
            }
            FileWriter fileWriter;
            try {
                fileWriter = new FileWriter(tempFile);
            } catch (IOException e) {
                log.error("write-to-temp-file fail", e);
                tempFileService.removeTempFile(tempFile);
                break;
            }
            wekaArffService.writeLoanHistoryToFile(new PrintWriter(fileWriter), bank.getCode());
            try {
                fileWriter.close();
            } catch (IOException e) {
                // NOTE: really want to?
            }
            // train the model
            LinearRegression model;
            try {
                ConverterUtils.DataSource source = new ConverterUtils.DataSource(tempFile.getAbsolutePath());
                Instances dataset = source.getDataSet();
                dataset.setClassIndex(dataset.numAttributes() - 1); // Column `amount`
                //
                model = new LinearRegression();
                model.buildClassifier(dataset);
                log.trace("Weka-LR = {}", model);
            } catch (Exception e) {
                log.error("linear regression model training fail", e);
                tempFileService.removeTempFile(tempFile);
                break;
            }
            //
            final byte[] modelBs = SerializationUtils.serialize(model);
            final String key = bank.getCode();
            trainedPredictionModelRepository.save(TrainedPredictionModel.builder()
                    .strategy(STRATEGY_NAME).key(key)
                    .data(modelBs)
                    .build());
            // cleanup temp file
            tempFileService.removeTempFile(tempFile);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public LoanAmountPrediction predict(
            Integer year, Integer month, String instituteCode
    ) throws NoDataException, PredictionNotPreparedException {
        Long amount = null;
        LinearRegression model;
        try {
            // load model
            final Optional<TrainedPredictionModel> trainedModel =
                    trainedPredictionModelRepository.findByStrategyAndKey(STRATEGY_NAME, instituteCode);
            if (!trainedModel.isPresent()) {
                throw new PredictionNotPreparedException("No trained model");
            }
            model = SerializationUtils.<LinearRegression>deserialize(trainedModel.get().getData());
            // predict
            final Instance instance = new DenseInstance(1.0, new double[]{year.doubleValue(), month.doubleValue(), 0.0});
            final double amountDouble = model.classifyInstance(instance);
            log.trace("Predicted amount = {}", amountDouble);
            amount = Double.valueOf(amountDouble).longValue();
        } catch (Exception e) {
            log.error("Prediction fail", e);
            throw new PredictionNotPreparedException("Prediction fail");
        }
        //
        return LoanAmountPrediction.builder()
                .year(year).month(month).bank(instituteCode)
                .amount(amount)
                .build();
    }
}
