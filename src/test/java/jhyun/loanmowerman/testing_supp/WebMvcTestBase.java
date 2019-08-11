package jhyun.loanmowerman.testing_supp;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

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
}
