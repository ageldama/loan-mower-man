package jhyun.loanmowerman.controllers;

import io.swagger.annotations.Api;
import jhyun.loanmowerman.controllers.api_user.ApiUserDuplicatedException;
import jhyun.loanmowerman.controllers.api_user.SignInForm;
import jhyun.loanmowerman.controllers.api_user.SignUpForm;
import jhyun.loanmowerman.security.JwtAuthenticationException;
import jhyun.loanmowerman.services.ApiUserService;
import jhyun.loanmowerman.services.JwtService;
import jhyun.loanmowerman.storage.entities.ApiUser;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

// TODO: swagger
@Api
@RequestMapping(path = "/api-user")
@RestController
public class ApiUserController {

    private ApiUserService apiUserService;

    private JwtService jwtService;

    @Autowired
    public ApiUserController(
            ApiUserService apiUserService,
            JwtService jwtService
    ) {
        this.apiUserService = apiUserService;
        this.jwtService = jwtService;
    }

    @RequestMapping(path = "/signup",
            method = POST,
            produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> signUp(
            @RequestBody SignUpForm signUpForm
    ) throws IOException, URISyntaxException, ApiUserDuplicatedException {
        final Optional<ApiUser> apiUser = apiUserService.signUp(signUpForm.getId(), signUpForm.getPassword());
        if (!apiUser.isPresent()) {
            throw new ApiUserDuplicatedException(signUpForm.getId());
        }
        val token = jwtService.generateToken(apiUser.get());
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Token", token)
                .body(apiUser.get().getId());
    }

    @RequestMapping(path = "/signin",
            method = POST,
            produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> signIn(
            @RequestBody SignInForm signInForm
    ) throws IOException, URISyntaxException {
        val apiUser = apiUserService.signIn(signInForm.getId(), signInForm.getPassword());
        if (apiUser.isPresent()) {
            val token = jwtService.generateToken(apiUser.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Token", token)
                    .body(apiUser.get().getId());
        } else {
            throw new JwtAuthenticationException("sign-in denied", null);
        }
    }

    @RequestMapping(path = "/refresh",
            method = GET,
            produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> refresh() throws IOException, URISyntaxException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<ApiUser> apiUser = jwtService.verify((String) authentication.getCredentials());
        if (apiUser.isPresent()) {
            val token = jwtService.generateToken(apiUser.get());
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Token", token)
                    .body(apiUser.get().getId());
        } else {
            throw new JwtAuthenticationException("refresh denied", null);
        }
    }

}