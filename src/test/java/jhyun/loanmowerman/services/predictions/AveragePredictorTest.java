package jhyun.loanmowerman.services.predictions;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.testing_supp.Examples;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AveragePredictorTest {

    @Autowired
    private AveragePredictor averagePredictor;

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Before
    public void prepare() throws IOException {
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(inputStream);
    }

    @After
    public void teardown() {
        loanAmountHistoryService.purgeAll();
    }

    @Test
    public void predict() throws NoDataException, PredictionNotPreparedException {
        final LoanAmountPrediction result = averagePredictor.predict(2018, 2, "2");
        assertThat(result).isNotNull();
        assertThat(result.getBank()).isNotBlank().isEqualTo("2");
        assertThat(result.getYear()).isEqualTo(2018);
        assertThat(result.getMonth()).isEqualTo(2);
        assertThat(result.getAmount()).isNotNull().isPositive();
    }

    @Test
    public void predictWithNoData() throws NoDataException, PredictionNotPreparedException {
        final LoanAmountPrediction result = averagePredictor.predict(2018, 24, "INVALID_BANK");
        assertThat(result).isNotNull();
        assertThat(result.getBank()).isNotBlank();
        assertThat(result.getYear()).isEqualTo(2018);
        assertThat(result.getMonth()).isEqualTo(24);
        assertThat(result.getAmount()).isNull(); // unable to predict!
    }
}