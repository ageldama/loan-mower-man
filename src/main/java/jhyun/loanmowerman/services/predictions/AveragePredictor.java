package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 해당 은행의 매년 같은 달의 지원금액 평균으로 예측.
 */
@Service
public class AveragePredictor implements Predictor, PredictionPrepper {
    private LoanAmountRepository loanAmountRepository;

    @Autowired
    public AveragePredictor(LoanAmountRepository loanAmountRepository) {
        this.loanAmountRepository = loanAmountRepository;
    }

    @Override
    public void prepare() {
        // No-ops
    }

    @Override
    public LoanAmountPrediction predict(
            Integer year, Integer month, String instituteCode
    ) throws NoDataException, PredictionNotPreparedException {
        final Long avg = loanAmountRepository.averageAmountOfMonthByInstitute(instituteCode, month);
        return LoanAmountPrediction.builder()
                .year(year).month(month).bank(instituteCode)
                .amount(avg)
                .build();
    }
}
