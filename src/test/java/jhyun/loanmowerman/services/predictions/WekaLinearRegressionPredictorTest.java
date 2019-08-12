package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.TempFileService;
import jhyun.loanmowerman.services.WekaArffService;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class WekaLinearRegressionPredictorTest {

    private PredictorMockRecorders mockRecorders = new PredictorMockRecorders();

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

    @Test
    public void predict() throws NoDataException, PredictionNotPreparedException {
        mockRecorders.recordLoanAmountHistory_findByInstituteOrderByYearAsc(loanAmountRepository, "BANKCODE", "BANKNAME");
        final LoanAmountPrediction result = predictor.predict(1982, 10, "BANKCODE");
        assertThat(result.getBank()).isNotBlank().isEqualTo("BANKCODE");
        assertThat(result.getYear()).isEqualTo(1982);
        assertThat(result.getMonth()).isEqualTo(10);
        assertThat(result.getAmount()).isNotNull().isPositive().isBetween(21L, 34L);
    }
}