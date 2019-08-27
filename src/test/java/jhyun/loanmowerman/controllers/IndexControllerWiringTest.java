package jhyun.loanmowerman.controllers;

import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.services.LoanAmountPredictionService;
import jhyun.loanmowerman.testing_supp.Examples;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class IndexControllerWiringTest {

    @Mock
    private LoanAmountHistoryService loanAmountHistoryService;

    @Mock
    private LoanAmountPredictionService loanAmountPredictionService;

    @InjectMocks
    private IndexController indexController;

    @Test
    public void testPurgeAll() {
        indexController.purgeDb();
        verify(loanAmountHistoryService, times(1)).purgeAll();
    }

    @Test
    public void testSaveCsv() throws IOException, URISyntaxException {
        indexController.putCsv(Examples.urlAsString(Examples.exampleCsv3Lines()));
        verify(loanAmountHistoryService, times(1)).saveCsv(any(Reader.class));
        verify(loanAmountPredictionService, times(1)).prepareForStrategies();
    }
}
