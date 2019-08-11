package jhyun.loanmowerman.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.predictions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
public class LoanAmountPredictionService {

    private AveragePredictor averagePredictor;
    private AverageAllPredictor averageAllPredictor;

    private Map<String, Predictor> predictorStrategies;
    private List<PredictionPrepper> predictionPreppers;

    private ExecutorService predictionPrepperExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public LoanAmountPredictionService(
            AveragePredictor averagePredictor,
            AverageAllPredictor AverageAllPredictor
    ) {
        this.averagePredictor = averagePredictor;
        this.averageAllPredictor = AverageAllPredictor;
        //
        predictorStrategies = ImmutableMap.of(
                "average", averagePredictor,
                "average_all", averageAllPredictor
        );
        predictionPreppers = ImmutableList.of(averagePredictor, averageAllPredictor);
    }

    public Future<?> prepareForStrategies() {
        return predictionPrepperExecutor.submit(() -> {
            log.info("Prediction preparation initiated: {}", this.predictionPreppers);
            for (PredictionPrepper prepper : this.predictionPreppers) {
                log.info("Prediction preparation starting: {}", prepper);
                prepper.prepare();
                log.info("Prediction preparation done: {}", prepper);
            }
            log.info("Prediction preparation all finished.");
        });
    }

    public LoanAmountPrediction predict(
            final String predictorStrategy,
            final Integer year,
            final Integer month,
            final String instituteCode
    ) throws NoSuchPredictorStrategyException, NoDataException, PredictionNotPreparedException {
        if (!predictorStrategies.containsKey(predictorStrategy)) {
            throw new NoSuchPredictorStrategyException(predictorStrategy);
        }
        final Predictor predictor = predictorStrategies.get(predictorStrategy);
        return predictor.predict(year, month, instituteCode);
    }

}
