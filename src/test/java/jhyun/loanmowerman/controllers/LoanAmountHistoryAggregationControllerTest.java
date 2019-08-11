package jhyun.loanmowerman.controllers;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.testing_supp.Examples;
import jhyun.loanmowerman.testing_supp.WebMvcTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class LoanAmountHistoryAggregationControllerTest extends WebMvcTestBase {

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    private ApiUserService apiUserService;

    @Before
    public void prepare() throws IOException {
        apiUserService.signUp(apiUserId, apiUserPw);
        final InputStream inputStream = Examples.urlAsInputStream(Examples.exampleCsv());
        loanAmountHistoryService.saveCsv(inputStream);
    }

    @After
    public void teardown() {
        apiUserService.purgeAllApiUsers();
        loanAmountHistoryService.purgeAll();
    }

    /**
     * 특정연도 지원 금액 큰 은행
     */
    @Test
    public void testMostLoanAllowedInstitute() {
        final HttpHeaders headers = apiUserJwtHeaders();
        final HttpEntity request = new HttpEntity(headers);
        final ResponseEntity<String> response = restTemplate.exchange(
                apiBase() + "/aggregations/mostOf/2017", HttpMethod.GET,
                request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        //
        log.trace("{}", response.getBody());
        final ReadContext ctx = JsonPath.parse(response.getBody());
        assertThat(ctx.<Object>read("$.year")).isEqualTo(2017);
        assertThat(ctx.<Object>read("$.bank")).isEqualTo("주택도시기금");
    }

    /**
     * 특정연도 지원 금액 큰 은행 -- not found
     */
    @Test
    public void testMostLoanAllowedInstituteNotFound() {
        final HttpHeaders headers = apiUserJwtHeaders();
        final HttpEntity request = new HttpEntity(headers);
        assertThatThrownBy(() ->
                restTemplate.exchange(
                        apiBase() + "/aggregations/mostOf/9999", HttpMethod.GET,
                        request, String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(o -> Objects.equals(((HttpClientErrorException) o).getStatusCode(),
                        HttpStatus.FAILED_DEPENDENCY));
    }

    /** 연도별 각 금융기관의 지원금액 합계 */
    @Test
    public void testTotalByYears() {
        final HttpHeaders headers = apiUserJwtHeaders();
        final HttpEntity request = new HttpEntity(headers);
        final ResponseEntity<String> response = restTemplate.exchange(
                apiBase() + "/aggregations/totalByYears", HttpMethod.GET,
                request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        //
        final ReadContext ctx = JsonPath.parse(response.getBody());
        assertThat(ctx.<Object>read("$.name")).isEqualTo("주택금융 공급현황");
        assertThat(ctx.<Integer>read("$.entries.length()")).isEqualTo(13);
        assertThat(ctx.<Integer>read("$.entries[0].detail_amount.length()")).isPositive();
        assertThat(ctx.<Integer>read("$.entries[0].total_amount")).isPositive();
        assertThat(ctx.<String>read("$.entries[0].year")).matches("^\\d+ 년$");
    }

    /**
     * 외환은행의 지급액 평균(연도별) 최소/최대값?
     */
    @Test
    public void testOehwaneunhaeng() {
        final HttpHeaders headers = apiUserJwtHeaders();
        final HttpEntity request = new HttpEntity(headers);
        final ResponseEntity<String> response = restTemplate.exchange(
                apiBase() + "/aggregations/oehwaneunhaeng", HttpMethod.GET,
                request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        //
        log.trace("{}", response.getBody());
        final ReadContext ctx = JsonPath.parse(response.getBody());
        assertThat(ctx.<Object>read("$.bank")).isEqualTo("외환은행");
        assertThat(ctx.<Object>read("$.support_amount.length()")).isEqualTo(2);
        assertThat(ctx.<Object>read("$.support_amount[0].year")).isEqualTo(2017);
        assertThat(ctx.<Object>read("$.support_amount[1].year")).isEqualTo(2015);
        assertThat(ctx.<Object>read("$.support_amount[0].amount")).isEqualTo(0);
        assertThat(ctx.<Object>read("$.support_amount[1].amount")).isEqualTo(20421);
    }

}