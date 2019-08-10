package jhyun.loanmowerman.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jhyun.loanmowerman.services.LoanAmountHistoryService;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import jhyun.loanmowerman.storage.repositories.LoanAmountRepository;
import jhyun.loanmowerman.testing_supp.Examples;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndexControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private InstituteRepository instituteRepository;

    @Autowired
    private LoanAmountRepository loanAmountRepository;

    @Autowired
    private LoanAmountHistoryService loanAmountHistoryService;

    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        val httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    private String apiBase() {
        return String.format("http://localhost:%s", port);
    }

    @Test
    public void testPurgeAllOk() {
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/history", HttpMethod.DELETE,
                        null, String.class);
        assertThat(response.getStatusCodeValue()).isEqualTo(205);
    }

    @Test
    public void testSaveCsvOk() throws URISyntaxException, IOException {
        assertThat(instituteRepository.count()).isZero();
        assertThat(loanAmountRepository.count()).isZero();
        //
        final RequestEntity<String> request = RequestEntity.put(new URI(apiBase() + "/history"))
                .contentType(MediaType.TEXT_PLAIN)
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
        this.loanAmountHistoryService.saveCsv(Examples.urlAsInputStream(exampleCsv));
    }

    private void purgeAll() {
        this.loanAmountHistoryService.purgeAll();
    }

    @Test
    public void testListAllInstituteNames() throws IOException {
        purgeAll();
        saveCsv(Examples.exampleCsv3Lines());
        //
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/institute-names", HttpMethod.GET,
                        null, String.class);
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