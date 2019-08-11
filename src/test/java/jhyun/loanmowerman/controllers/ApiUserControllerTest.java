package jhyun.loanmowerman.controllers;

import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.testing_supp.WebMvcTestBase;
import lombok.val;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


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
        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    // TODO: testSignUpDup

    // TODO: testSignInOk

    // TODO: testSignInNotFound

    // TODO: testSignInWrongPassword


}