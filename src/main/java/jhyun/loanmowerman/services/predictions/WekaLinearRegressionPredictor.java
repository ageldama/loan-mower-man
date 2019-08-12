package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.TempFileService;
import jhyun.loanmowerman.services.WekaArffService;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.functions.LinearRegression;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 해당 은행의 지원금액 내역을 Weka Linear Regression으로 예측
 */
@Slf4j
@Service
public class WekaLinearRegressionPredictor implements Predictor, PredictionPrepper {

    private TempFileService tempFileService;
    private WekaArffService wekaArffService;

    @Autowired
    public WekaLinearRegressionPredictor(
            TempFileService tempFileService,
            WekaArffService wekaArffService
    ) {
        this.tempFileService = tempFileService;
        this.wekaArffService = wekaArffService;
    }

    @Override
    public void prepare() {
        // No-ops
    }

    @Override
    public LoanAmountPrediction predict(
            Integer year, Integer month, String instituteCode
    ) throws NoDataException, PredictionNotPreparedException {
        // prepare temp file
        File tempFile;
        try {
            tempFile = tempFileService.createTempFile(".arff");
        } catch (IOException e) {
            throw new PredictionNotPreparedException("cannot create temporary '.arff' file");
        }
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(tempFile);
        } catch (IOException e) {
            throw new PredictionNotPreparedException("cannot open file: " + tempFile.toString());
        }
        wekaArffService.writeLoanHistoryToFile(new PrintWriter(fileWriter), instituteCode);
        try {
            fileWriter.close();
        } catch (IOException e) {
            // NOTE: really want to?
        }
        // predict
        Long amount = null;
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(tempFile.getAbsolutePath());
            Instances dataset = source.getDataSet();
            dataset.setClassIndex(dataset.numAttributes() - 1); // Column `amount`
            //
            LinearRegression model = new LinearRegression();
            model.buildClassifier(dataset);
            log.trace("Weka-LR = {}", model);
            //
            final Instance instance = new DenseInstance(1.0, new double[]{year.doubleValue(), month.doubleValue(), 0.0});
            final double amountDouble = model.classifyInstance(instance);
            log.trace("Predicted amount = {}", amountDouble);
            amount = Double.valueOf(amountDouble).longValue();
        } catch (Exception e) {
            log.error("Prediction fail", e);
            throw new PredictionNotPreparedException("Prediction fail");
        }
        // cleanup temp file
        tempFileService.removeTempFile(tempFile);
        //
        return LoanAmountPrediction.builder()
                .year(year).month(month).bank(instituteCode)
                .amount(amount)
                .build();
    }
}
