package jhyun.loanmowerman.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jhyun.loanmowerman.controllers.api_user.SignInForm;
import jhyun.loanmowerman.controllers.api_user.SignUpForm;
import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.testing_supp.WebMvcTestBase;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class ApiUserControllerTest extends WebMvcTestBase {

    @Autowired
    private ApiUserService apiUserService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @After
    public void purgeAllApiUsers() {
        apiUserService.purgeAllApiUsers();
    }

    private HttpEntity<String> makeSignUpRequest(final String id, final String pw) {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String jsonBody = "";
        try {
            jsonBody = objectMapper.writeValueAsString(new SignUpForm(id, pw));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new HttpEntity<>(jsonBody, headers);
    }

    private HttpEntity<String> makeSignInRequest(final String id, final String pw) {
        val headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String jsonBody = "";
        try {
            jsonBody = objectMapper.writeValueAsString(new SignInForm(id, pw));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new HttpEntity<>(jsonBody, headers);
    }

    @Test
    public void testSignUpOk() {
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signup", HttpMethod.POST,
                        makeSignUpRequest("foo", "bar"), String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testSignUpDup() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        //
        assertThatThrownBy(() ->
                restTemplate.exchange(apiBase() + "/api-user/signup", HttpMethod.POST,
                        makeSignUpRequest("foo", "bar"), String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(o -> ((HttpClientErrorException) o).getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testSignInOk() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        //
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        makeSignInRequest("foo", "bar"), String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void testSignInNotFound() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        //
        assertThatThrownBy(() ->
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        makeSignInRequest("WRONG_ID", "bar"), String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(o -> ((HttpClientErrorException) o).getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testSignInWrongPassword() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        //
        assertThatThrownBy(() ->
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        makeSignInRequest("foo", "WRONG_PASSWORD"), String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(o -> ((HttpClientErrorException) o).getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testRefreshOk() throws InterruptedException {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        // initial sign-in
        ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        makeSignInRequest("foo", "bar"), String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        final String token1 = response.getHeaders().getFirst("Token");
        // access authorized endpoint with initial token
        final HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", String.format("Bearer %s", token1));
        final HttpEntity request2 = new HttpEntity(headers2);
        response =
                restTemplate.exchange(apiBase() + "/institute-names", HttpMethod.GET,
                        request2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // refresh the token
        response =
                restTemplate.exchange(apiBase() + "/api-user/refresh", HttpMethod.GET,
                        request2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final String token2 = response.getHeaders().getFirst("Token");
        assertThat(token2).isNotBlank();
        // access authorized endpoint with refreshed token
        final HttpHeaders headers3 = new HttpHeaders();
        headers2.add("Authorization", String.format("Bearer %s", token2));
        final HttpEntity request3 = new HttpEntity(headers3);
        response =
                restTemplate.exchange(apiBase() + "/institute-names", HttpMethod.GET,
                        request3, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testRefreshWithInvalidToken() throws InterruptedException {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        // refresh the token
        final HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", String.format("Bearer %s", "foobar?"));
        final HttpEntity request2 = new HttpEntity(headers2);
        assertThatThrownBy(() ->
                restTemplate.exchange(apiBase() + "/api-user/refresh", HttpMethod.GET,
                        request2, String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(o -> ((HttpClientErrorException) o).getStatusCode().equals(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void testAuthenticationOk() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        // signin
        ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        makeSignInRequest("foo", "bar"), String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        // access authorized endpoint
        final HttpHeaders headers2 = new HttpHeaders();
        final String token = response.getHeaders().getFirst("Token");
        headers2.add("Authorization", String.format("Bearer %s", token));
        final HttpEntity request2 = new HttpEntity(headers2);
        response =
                restTemplate.exchange(apiBase() + "/institute-names", HttpMethod.GET,
                        request2, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testAuthenticationFail() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        // signin
        ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        makeSignInRequest("foo", "bar"), String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        // access authorized endpoint
        final HttpHeaders headers2 = new HttpHeaders();
        final String token = response.getHeaders().getFirst("Token") + "USELESS_STRING";
        headers2.add("Authorization", String.format("Bearer %s", token));
        final HttpEntity request2 = new HttpEntity(headers2);
        assertThatThrownBy(() ->
                restTemplate.exchange(apiBase() + "/institute-names", HttpMethod.GET,
                        request2, String.class))
                .isInstanceOf(HttpClientErrorException.class)
                .satisfies(o -> HttpStatus.FORBIDDEN.equals(((HttpClientErrorException) o).getStatusCode()));
    }
}