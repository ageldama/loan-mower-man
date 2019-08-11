package jhyun.loanmowerman.controllers;

import com.google.common.collect.ImmutableList;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.controllers.predictions.LoanAmountPredictionOfMonthRequest;
import jhyun.loanmowerman.services.LoanAmountPredictionService;
import jhyun.loanmowerman.services.predictions.LoanAmountPrediction;
import jhyun.loanmowerman.services.predictions.NoSuchPredictorStrategyException;
import jhyun.loanmowerman.services.predictions.PredictionNotPreparedException;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoanAmountPredictionControllerWiringTest {

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private LoanAmountPredictionService loanAmountPredictionService;

    @InjectMocks
    private LoanAmountPredictionController loanAmountPredictionController;

    @Test
    public void predict() throws NoDataException, PredictionNotPreparedException, NoSuchPredictorStrategyException {
        given(instituteRepository.findInstituteCodeByName(anyString()))
                .willReturn(ImmutableList.of("09091"));
        //
        final LoanAmountPrediction result = loanAmountPredictionController.predict(
                LoanAmountPredictionOfMonthRequest.builder()
                        .bank("DB")
                        .month(13)
                        .build(),
                "average"
        );
        //
        verify(instituteRepository, times(1)).findInstituteCodeByName(eq("DB"));
        verify(loanAmountPredictionService, times(1))
                .predict(eq("average"), eq(2018), eq(13), eq("09091"));
    }
}