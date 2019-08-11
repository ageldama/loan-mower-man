package jhyun.loanmowerman.services;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.predictions.AveragePredictor;
import jhyun.loanmowerman.services.predictions.LoanAmountPrediction;
import jhyun.loanmowerman.services.predictions.NoSuchPredictorStrategyException;
import jhyun.loanmowerman.services.predictions.PredictionNotPreparedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoanAmountPredictionServiceTest {

    @Mock
    private AveragePredictor averagePredictor;

    @InjectMocks
    private LoanAmountPredictionService loanAmountPredictionService;

    @Test
    public void prepareForStrategies() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<?> fut = loanAmountPredictionService.prepareForStrategies();
        fut.get(5L, TimeUnit.SECONDS);
        verify(averagePredictor, times(1)).prepare();
    }

    @Test
    public void predictWithAverageStrategy() throws NoSuchPredictorStrategyException, NoDataException, PredictionNotPreparedException {
        final LoanAmountPrediction prediction = loanAmountPredictionService.predict(
                "average",
                2018, 2, "bnk");
        verify(averagePredictor, times(1))
                .predict(eq(2018), eq(2), eq("bnk"));
    }

    @Test(expected = NoSuchPredictorStrategyException.class)
    public void predictWithInvalidStrategy() throws NoSuchPredictorStrategyException, NoDataException, PredictionNotPreparedException {
        loanAmountPredictionService.predict("INVALID_STRATEGY",
                9999, 13, "bnk");
    }
}