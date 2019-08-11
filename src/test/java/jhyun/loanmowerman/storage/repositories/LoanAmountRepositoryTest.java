package jhyun.loanmowerman.storage.repositories;

import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.testing_supp.Examples;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore("SpringBootTest이라 느리기도 하고, 한번 Repository 쿼리 동작하는거 확인해서 일단은 더 이상 필요없음")
@RunWith(SpringRunner.class)
@SpringBootTest
public class LoanAmountRepositoryTest {

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    private LoanAmountRepository loanAmountRepository;

    @Before
    public void prepare() throws IOException {
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv3Lines());
        loanAmountHistoryService.saveCsv(inputStream);
    }

    @After
    public void teardown() {
        loanAmountHistoryService.purgeAll();
    }

    @Transactional(readOnly = true)
    @Test
    public void testFindByInstituteOrderByYearAsc() {
        try (Stream<LoanAmount> stream = loanAmountRepository.findByInstituteOrderByYearAsc("2")) {
            final List<LoanAmount> result = stream.collect(Collectors.toList());
            final Institute institute = new Institute("2", "주택도시기금");
            assertThat(result).isNotNull()
                    // NOTE: not really correct assertion:
                    .containsExactly(new LoanAmount(institute, 2005, 1, 1019),
                            new LoanAmount(institute, 2005, 2, 1144),
                            new LoanAmount(institute, 2005, 3, 1144));
        }
    }
}