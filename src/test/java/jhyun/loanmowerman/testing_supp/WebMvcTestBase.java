package jhyun.loanmowerman.testing_supp;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebMvcTestBase {
    @LocalServerPort
    protected int port;

    protected RestTemplate restTemplate;

    @Before
    public void setUp() {
        val httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    protected String apiBase() {
        return String.format("http://localhost:%s", port);
    }

    protected final String apiUserId = "foo";
    protected final String apiUserPw = "bar";

    protected HttpHeaders apiUserJwtHeaders() {
        // signin
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        HashMap form = new HashMap();
        form.put("id", apiUserId);
        form.put("password", apiUserPw);
        HttpEntity request = new HttpEntity(form, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        request, String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        //
        final HttpHeaders headers2 = new HttpHeaders();
        final String token = response.getHeaders().getFirst("Token");
        headers2.add("Authorization", String.format("Bearer %s", token));
        return headers2;
    }
}
