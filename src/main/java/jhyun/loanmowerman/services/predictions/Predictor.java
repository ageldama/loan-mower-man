package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;

public interface Predictor {
    LoanAmountPrediction predict(
            final Integer year,
            final Integer month,
            final String instituteCode
    ) throws NoDataException, PredictionNotPreparedException;
}
