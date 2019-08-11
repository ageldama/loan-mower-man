package jhyun.loanmowerman.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.predictions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class LoanAmountPredictionService {

    private AveragePredictor averagePredictor;

    private Map<String, Predictor> predictorStrategies;
    private List<PredictionPrepper> predictionPreppers;

    private ExecutorService predictionPrepperExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    public LoanAmountPredictionService(AveragePredictor averagePredictor) {
        this.averagePredictor = averagePredictor;
        //
        predictorStrategies = ImmutableMap.of(
                "average", averagePredictor
        );
        predictionPreppers = ImmutableList.of(averagePredictor);
    }

    public void prepareForStrategies() {
        predictionPrepperExecutor.submit(() -> {
            for (PredictionPrepper prepper : this.predictionPreppers) {
                prepper.prepare();
            }
        });
    }

    public LoanAmountPrediction predict(
            final String predictorStrategy,
            final Integer year,
            final Integer month,
            final Integer instituteCode
    ) throws NoSuchPredictorStrategyException, NoDataException {
        if (!predictorStrategies.containsKey(predictorStrategy)) {
            throw new NoSuchPredictorStrategyException(predictorStrategy);
        }
        final Predictor predictor = predictorStrategies.get(predictorStrategy);
        return predictor.predict(year, month, instituteCode);
    }

}
