package jhyun.loanmowerman.services;

import com.google.common.collect.ImmutableMap;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.MinMaxOfInstitute;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.TotalLoanAmountEntry;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.TotalLoanAmounts;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.YearAndAmountEntry;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.testing_supp.Examples;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
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
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsvMini2());
        loanAmountHistoryService.saveCsv(inputStream);
        //
        final TotalLoanAmounts result = loanAmountHistoryAggregationService.totalLoanAmountsByYear();
        //
        assertThat(result.getName()).isEqualTo("주택금융 공급현황");
        assertThat(result.getEntries()).hasSize(2)
                .containsExactly(
                        TotalLoanAmountEntry.builder()
                                .year("2005 년").total(84L)
                                .amounts(ImmutableMap.<String, Long>of("ABC", 60L, "DEF", 24L))
                                .build(),
                        TotalLoanAmountEntry.builder()
                                .year("2017 년").total(13106L)
                                .amounts(ImmutableMap.<String, Long>of("ABC", 1110L, "DEF", 11996L))
                                .build());
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

    @Test
    public void testFindMinMaxOfInstituteOnYear() throws IOException {
        loanAmountHistoryService.purgeAll();
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(inputStream);
        //
        final MinMaxOfInstitute result = loanAmountHistoryAggregationService.findMinMaxOfInstitute("외환은행");
        assertThat(result.getInstituteName()).isEqualTo("외환은행");
        assertThat(result.getMinMax()).hasSize(2)
                .containsExactly(
                        YearAndAmountEntry.builder().year(2017).amount(0L).build(),
                        YearAndAmountEntry.builder().year(2015).amount(20421L).build());
    }
}