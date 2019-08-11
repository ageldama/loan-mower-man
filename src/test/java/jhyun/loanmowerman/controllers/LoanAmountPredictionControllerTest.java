package jhyun.loanmowerman.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.controllers.predictions.LoanAmountPredictionOfMonthRequest;
import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.services.LoanAmountPredictionService;
import jhyun.loanmowerman.services.predictions.AveragePredictor;
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
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(inputStream);
        Future<?> fut = loanAmountPredictionService.prepareForStrategies();
        fut.get(5L, TimeUnit.SECONDS);
    }

    @After
    public void teardown() {
        apiUserService.purgeAllApiUsers();
        loanAmountHistoryService.purgeAll();
    }

    @Test
    public void testPredictWithAverage() throws JsonProcessingException, NoDataException, PredictionNotPreparedException {
        final LoanAmountPredictionOfMonthRequest request = LoanAmountPredictionOfMonthRequest.builder()
                .bank("국민은행")
                .month(2)
                .build();
        final HttpHeaders headers = apiUserJwtHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpEntity request2 = new HttpEntity(objectMapper.writeValueAsString(request), headers);
        final ResponseEntity<String> response = restTemplate.exchange(
                apiBase() + "/predictions/?strategy=average", HttpMethod.POST,
                request2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info("prediction-result = {}", response.getBody());
        //
        verify(averagePredictor, times(1)).predict(
                eq(2018), eq(2), eq("3")
        );
    }
}