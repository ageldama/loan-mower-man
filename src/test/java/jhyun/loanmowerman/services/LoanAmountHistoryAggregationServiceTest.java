package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.testing_supp.Examples;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoanAmountHistoryAggregationServiceTest {

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    private LoanAmountHistoryAggregationService loanAmountHistoryAggregationService;

    @Test
    public void testTotalLoanAmountsByYear() throws IOException {
        loanAmountHistoryService.purgeAll();
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(inputStream);
        //
        final Map<String, Object> result = loanAmountHistoryAggregationService.totalLoanAmountsByYear();
        // TODO 값 검증
    }

    @Test
    public void testMostLoanAllowedInstitute() throws IOException {
        loanAmountHistoryService.purgeAll();
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(inputStream);
        //
        Optional<Institute> institute = loanAmountHistoryAggregationService.mostLoanAllowedInstitute(2005);
        assertThat(institute).isPresent();
        assertThat(institute.get().getCode()).isEqualTo("2");
        assertThat(institute.get().getName()).isEqualTo("주택도시기금");
    }
}