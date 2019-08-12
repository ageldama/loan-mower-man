package jhyun.loanmowerman.services.predictions;

import com.google.common.collect.ImmutableList;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.TempFileService;
import jhyun.loanmowerman.services.WekaArffService;
import jhyun.loanmowerman.storage.entities.Institute;
import jhyun.loanmowerman.storage.entities.TrainedPredictionModel;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.storage.repositories.TrainedPredictionModelRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class WekaLinearRegressionPredictorTest {

    private PredictorMockRecorders mockRecorders = new PredictorMockRecorders();

    @Mock
    private LoanAmountRepository loanAmountRepository;

    @Mock
    private TrainedPredictionModelRepository trainedPredictionModelRepository;

    @Mock
    private InstituteRepository instituteRepository;

    private WekaLinearRegressionPredictor predictor;

    @Before
    public void setUp() throws Exception {
        predictor = new WekaLinearRegressionPredictor(
                new TempFileService(),
                new WekaArffService(loanAmountRepository),
                trainedPredictionModelRepository,
                instituteRepository
        );
    }

    private void recordInstituteRepositoryMock() {
        given(instituteRepository.findAll())
                .willReturn(ImmutableList.of(new Institute("BANKCODE", "BANKNAME")));
    }

    private TrainedPredictionModel savedModel = null;

    private void recordTrainedPredictionModelRepository() {
        given(trainedPredictionModelRepository.save(any(TrainedPredictionModel.class)))
                .will(invocation -> {
                    TrainedPredictionModel model = invocation.getArgument(0);
                    savedModel = model;
                    return model;
                });

        given(trainedPredictionModelRepository.findByStrategyAndKey(anyString(), anyString()))
                .will(invocation -> Optional.ofNullable(savedModel));
    }

    @Test
    public void predict() throws NoDataException, PredictionNotPreparedException {
        recordInstituteRepositoryMock();
        recordTrainedPredictionModelRepository();
        mockRecorders.recordLoanAmountHistory_findByInstituteOrderByYearAsc(loanAmountRepository, "BANKCODE", "BANKNAME");
        //
        predictor.prepare();
        final LoanAmountPrediction result = predictor.predict(1982, 10, "BANKCODE");
        assertThat(result.getBank()).isNotBlank().isEqualTo("BANKCODE");
        assertThat(result.getYear()).isEqualTo(1982);
        assertThat(result.getMonth()).isEqualTo(10);
        assertThat(result.getAmount()).isNotNull().isPositive().isBetween(21L, 34L);
    }
}