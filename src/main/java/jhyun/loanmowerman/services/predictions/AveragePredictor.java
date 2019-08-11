package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import org.springframework.stereotype.Service;

@Service
public class AveragePredictor implements Predictor, PredictionPrepper {
    @Override
    public void prepare() {
        // No-ops
    }

    @Override
    public LoanAmountPrediction predict(
            Integer year, Integer month, Integer instituteCode
    ) throws NoDataException {
        // TODO
        return null;
    }
}
