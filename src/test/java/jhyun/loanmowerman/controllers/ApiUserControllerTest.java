package jhyun.loanmowerman.controllers;

import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.testing_supp.WebMvcTestBase;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ApiUserControllerTest extends WebMvcTestBase {

    @Autowired
    private ApiUserService apiUserService;

    @After
    public void purgeAllApiUsers() {
        apiUserService.purgeAllApiUsers();
    }

    @Test
    public void testSignUpOk() {
        val headers = new LinkedMultiValueMap<String, String>();

        val form = new HashMap();
        form.put("id", "foo");
        form.put("password", "bar");

        val request = new HttpEntity(form, headers);

        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signup", HttpMethod.POST,
                        request, String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testSignUpDup() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        //
        val headers = new LinkedMultiValueMap<String, String>();
        val form = new HashMap();
        form.put("id", "foo");
        form.put("password", "bar");
        val request = new HttpEntity(form, headers);
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signup", HttpMethod.POST,
                        request, String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
    }

    @Test
    public void testSignInOk() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        //
        val headers = new LinkedMultiValueMap<String, String>();
        val form = new HashMap();
        form.put("id", "foo");
        form.put("password", "bar");
        val request = new HttpEntity(form, headers);
        final ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        request, String.class);
        assertThat(response.getHeaders()).containsKey("Token");
        assertThat(response.getHeaders().getFirst("Token")).isNotBlank();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }


    // TODO: testSignInNotFound

    // TODO: testSignInWrongPassword

    // TODO: refreshOk

    // TODO: refreshInvalidToken


    @Test
    public void testAuthenticationOk() {
        // create: 'foo'
        apiUserService.signUp("foo", "bar");
        // signin
        LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        HashMap form = new HashMap();
        form.put("id", "foo");
        form.put("password", "bar");
        HttpEntity request = new HttpEntity(form, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(apiBase() + "/api-user/signin", HttpMethod.POST,
                        request, String.class);
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

    // TODO: authenticationFail
}