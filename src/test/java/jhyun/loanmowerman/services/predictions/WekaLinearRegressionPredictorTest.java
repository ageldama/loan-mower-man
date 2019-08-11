package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.TempFileService;
import jhyun.loanmowerman.services.WekaArffService;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WekaLinearRegressionPredictorTest {

    @Mock
    private LoanAmountRepository loanAmountRepository;

    private WekaLinearRegressionPredictor predictor;

    @Before
    public void setUp() throws Exception {
        predictor = new WekaLinearRegressionPredictor(
                new TempFileService(),
                new WekaArffService(loanAmountRepository)
        );
    }

    private void recordLoanAmountHistory(String bankCode, String bankName) {
        final Institute institute = new Institute(bankCode, bankName);
        given(loanAmountRepository.findByInstituteOrderByYearAsc(anyString())).willReturn(
                Stream.of(
                        new LoanAmount(institute, 1982, 1, 1),
                        new LoanAmount(institute, 1982, 2, 1),
                        new LoanAmount(institute, 1982, 3, 2),
                        new LoanAmount(institute, 1982, 4, 3),
                        new LoanAmount(institute, 1982, 5, 5),
                        new LoanAmount(institute, 1982, 6, 8),
                        new LoanAmount(institute, 1982, 7, 13),
                        new LoanAmount(institute, 1982, 8, 21),
                        new LoanAmount(institute, 1982, 9, 34)
                )
        );
    }

    @Test
    public void predict() throws NoDataException, PredictionNotPreparedException {
        recordLoanAmountHistory("BANKCODE", "BANKNAME");
        final LoanAmountPrediction result = predictor.predict(1982, 10, "BANKCODE");
        assertThat(result.getBank()).isNotBlank().isEqualTo("BANKCODE");
        assertThat(result.getYear()).isEqualTo(1982);
        assertThat(result.getMonth()).isEqualTo(10);
        assertThat(result.getAmount()).isNotNull().isPositive().isBetween(21L, 34L);
    }
}