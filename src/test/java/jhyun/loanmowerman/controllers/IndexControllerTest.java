package jhyun.loanmowerman.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.testing_supp.Examples;
import jhyun.loanmowerman.testing_supp.WebMvcTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class IndexControllerTest extends WebMvcTestBase {
    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private LoanAmountRepository loanAmountRepository;

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    @Autowired
    private ApiUserService apiUserService;

    @Before
    public void prepareApiUser() {
        apiUserService.purgeAllApiUsers();
        apiUserService.signUp(apiUserId, apiUserPw);
    }

    @Test
    public void testPurgeAllOk() {
        final HttpHeaders headers = apiUserJwtHeaders();
        final HttpEntity request = new HttpEntity(headers);
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/history", HttpMethod.DELETE,
                        request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(205);
    }

    @Test
    public void testSaveCsvOk() throws URISyntaxException, IOException {
        assertThat(instituteRepository.count()).isZero();
        assertThat(loanAmountRepository.count()).isZero();
        final HttpHeaders headers = apiUserJwtHeaders();
        //
        final RequestEntity<String> request = RequestEntity
                .put(new URI(apiBase() + "/history"))
                .contentType(MediaType.TEXT_PLAIN)
                .header("Authorization", headers.getFirst("Authorization"))
                .body(Examples.urlAsString(Examples.exampleCsv3Lines()));

        final ResponseEntity<String> response = restTemplate.exchange(apiBase() + "/history", HttpMethod.PUT,
                request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(instituteRepository.count()).isNotZero();
        assertThat(loanAmountRepository.count()).isNotZero();
        //
        loanAmountHistoryService.purgeAll();
        assertThat(instituteRepository.count()).isZero();
        assertThat(loanAmountRepository.count()).isZero();
    }

    private void saveCsv(final URL exampleCsv) throws IOException {
        this.loanAmountHistoryService.saveCsv(Examples.urlAsReader(exampleCsv));
    }

    private void purgeAll() {
        this.loanAmountHistoryService.purgeAll();
    }

    @Test
    public void testListAllInstituteNames() throws IOException {
        purgeAll();
        saveCsv(Examples.exampleCsv3Lines());
        final HttpHeaders headers = apiUserJwtHeaders();
        //
        final HttpEntity request = new HttpEntity(headers);
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/institute-names", HttpMethod.GET,
                        request, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        final ObjectMapper objectMapper = new ObjectMapper();
        final ArrayList names = objectMapper.readValue(response.getBody(), ArrayList.class);
        log.debug("names = {}", names);
        assertThat(names).hasSize(9)
                .contains("기타은행"
                        , "주택도시기금"
                        , "국민은행"
                        , "우리은행"
                        , "신한은행"
                        , "한국시티은행"
                        , "하나은행"
                        , "농협은행/수협은행"
                        , "외환은행");
        //
        purgeAll();
    }

}