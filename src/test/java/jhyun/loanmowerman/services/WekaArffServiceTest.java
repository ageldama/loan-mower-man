package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WekaArffServiceTest {

    @Mock
    private LoanAmountRepository loanAmountRepository;

    @InjectMocks
    private WekaArffService wekaArffService;

    private void recordLoanAmountHistory(String bankCode, String bankName) {
        final Institute institute = new Institute(bankCode, bankName);
        given(loanAmountRepository.findByInstituteOrderByYearAsc(anyString())).willReturn(
                Stream.of(
                        new LoanAmount(institute, 1982, 10, 1234),
                        new LoanAmount(institute, 1982, 11, 5678),
                        new LoanAmount(institute, 1982, 12, 91011)
                )
        );
    }

    @Test
    public void writeLoanHistoryToFile() {
        recordLoanAmountHistory("BANKCODE", "BANKNAME");
        //
        final StringWriter stringWriter = new StringWriter();
        wekaArffService.writeLoanHistoryToFile(new PrintWriter(stringWriter), "BANKCODE");
        final String expect = "@RELATION loan\n" +
                "\n" +
                "@ATTRIBUTE year NUMERIC\n" +
                "@ATTRIBUTE month NUMERIC\n" +
                "@ATTRIBUTE amount NUMERIC\n" +
                "\n" +
                "@DATA\n" +
                "1982,10,1234\n" +
                "1982,11,5678\n" +
                "1982,12,91011\n";
        final String resultString = stringWriter.toString().replaceAll("\\r\\n", "\n"); // NOTE: 혹시나
        assertThat(resultString).isEqualTo(expect);
    }
}