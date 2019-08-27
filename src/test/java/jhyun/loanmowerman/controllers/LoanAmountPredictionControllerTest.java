package jhyun.loanmowerman.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.controllers.predictions.LoanAmountPredictionOfMonthRequest;
import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.services.LoanAmountPredictionService;
import jhyun.loanmowerman.services.predictions.AveragePredictor;
import jhyun.loanmowerman.services.predictions.LoanAmountPrediction;
import jhyun.loanmowerman.services.predictions.PredictionNotPreparedException;
import jhyun.loanmowerman.testing_supp.Examples;
import jhyun.loanmowerman.testing_supp.WebMvcTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
public class LoanAmountPredictionControllerTest extends WebMvcTestBase {

    @Autowired
    private ApiUserService apiUserService;

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    private LoanAmountPredictionService loanAmountPredictionService;

    @MockBean
    private AveragePredictor averagePredictor;

    @Before
    public void prepare() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        apiUserService.signUp(apiUserId, apiUserPw);
        final Reader reader = Examples.urlAsReader(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(reader);
        Future<?> fut = loanAmountPredictionService.prepareForStrategies();
        fut.get(5L, TimeUnit.SECONDS);
    }

    @After
    public void teardown() {
        apiUserService.purgeAllApiUsers();
        loanAmountHistoryService.purgeAll();
    }

    @Test
    public void testPredictWithAverage() throws IOException, NoDataException, PredictionNotPreparedException {
        given(averagePredictor.predict(any(), any(), anyString())).willReturn(
                LoanAmountPrediction.builder()
                        .amount(123L)
                        .bank("BANKCODE")
                        .year(1234)
                        .month(24)
                        .build()
        );
        //
        final ObjectMapper objectMapper = new ObjectMapper();
        //
        final LoanAmountPredictionOfMonthRequest request = LoanAmountPredictionOfMonthRequest.builder()
                .bank("국민은행")
                .month(2)
                .build();
        final HttpHeaders headers = apiUserJwtHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        final HttpEntity request2 = new HttpEntity(objectMapper.writeValueAsString(request), headers);
        final ResponseEntity<String> response = restTemplate.exchange(
                apiBase() + "/predictions/?strategy=average", HttpMethod.POST,
                request2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final LoanAmountPrediction predictionResult = objectMapper.readValue(response.getBody(), LoanAmountPrediction.class);
        assertThat(predictionResult).isNotNull();
        assertThat(predictionResult.getAmount()).isEqualTo(123L);
        assertThat(predictionResult.getBank()).isEqualTo("BANKCODE");
        assertThat(predictionResult.getYear()).isEqualTo(1234);
        assertThat(predictionResult.getMonth()).isEqualTo(24);
        //
        verify(averagePredictor, times(1)).predict(
                eq(2018), eq(2), eq("3")
        );
    }
}