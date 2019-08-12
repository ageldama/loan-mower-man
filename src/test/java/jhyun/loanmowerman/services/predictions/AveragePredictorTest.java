package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AveragePredictorTest {

    private PredictorMockRecorders mockRecorders = new PredictorMockRecorders();

    @Mock
    private LoanAmountRepository loanAmountRepository;

    @InjectMocks
    private AveragePredictor averagePredictor;

    private void recordMocks() {
        final Institute institute = new Institute("BANKCODE", "BANKNAME");
        given(loanAmountRepository.averageAmountOfMonthByInstitute(anyString(), any()))
                .will(invocation -> {
                    if (invocation.getArgument(0).equals("BANKCODE")) {
                        return Double.valueOf(Math.ceil(mockRecorders.loanAmounts(institute)
                                .mapToLong(LoanAmount::getAmount)
                                .average()
                                .getAsDouble())).longValue();
                    } else {
                        return null;
                    }
                });
    }

    @Test
    public void predict() throws NoDataException, PredictionNotPreparedException {
        recordMocks();
        //
        final LoanAmountPrediction result = averagePredictor.predict(1982, 10, "BANKCODE");
        assertThat(result).isNotNull();
        assertThat(result.getBank()).isNotBlank().isEqualTo("BANKCODE");
        assertThat(result.getYear()).isEqualTo(1982);
        assertThat(result.getMonth()).isEqualTo(10);
        assertThat(result.getAmount()).isNotNull().isBetween(9L, 10L);
    }

    @Test
    public void predictWithNoData() throws NoDataException, PredictionNotPreparedException {
        recordMocks();
        //
        final LoanAmountPrediction result = averagePredictor.predict(1982, 10, "INVALID_BANK");
        assertThat(result).isNotNull();
        assertThat(result.getBank()).isNotBlank();
        assertThat(result.getYear()).isEqualTo(1982);
        assertThat(result.getMonth()).isEqualTo(10);
        assertThat(result.getAmount()).isNull(); // unable to predict!
    }
}