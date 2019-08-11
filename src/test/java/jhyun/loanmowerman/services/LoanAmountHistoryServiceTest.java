package jhyun.loanmowerman.services;

import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.LoanAmount;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.testing_supp.Examples;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoanAmountHistoryServiceTest {

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private LoanAmountRepository loanAmountRepository;

    @InjectMocks
    private LoanAmountHistoryService loanAmountHistoryService;

    @Test
    public void testPurgeAll() {
        this.loanAmountHistoryService.purgeAll();
        verify(this.instituteRepository, times(1)).deleteAll();
        verify(this.loanAmountRepository, times(1)).deleteAll();
    }

    @Test
    public void testSaveCsvMini() throws IOException {
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsvMini());
        this.loanAmountHistoryService.saveCsv(inputStream);
        //
        verify(this.instituteRepository, times(2)).findById(eq("3"));
        verify(this.instituteRepository, times(1)).save(eq(new Institute("3", "국민은행")));
        verify(this.loanAmountRepository, times(1)).save(eq(new LoanAmount(
                new Institute("3", "국민은행"),
                2005, 1, 123
        )));
    }

    @Test
    public void testListAllInstituteNames() {
        this.loanAmountHistoryService.listAllInstituteNames();
        verify(this.instituteRepository, times(1)).listAllInstituteNames();
    }
}