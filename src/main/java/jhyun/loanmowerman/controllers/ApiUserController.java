package jhyun.loanmowerman.controllers;

import io.swagger.annotations.*;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Api(description = "API사용자 등록, JWT토큰 발급, JWT토큰 갱신")
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

    @ApiOperation(value = "새로운 API사용자 등록")
    @ApiResponses({
            @ApiResponse(code = 201, message = "등록 완료. 등록한 사용자ID을 본문으로 응답. 응답헤더의 `Token`으로 생성한 사용자를 위한 JWT토큰을 넣어 응답"),
            @ApiResponse(code = 400, message = "이미 지정한 ID의 사용자가 있을때")
    })
    @RequestMapping(path = "/signup",
            method = POST,
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> signUp(
            @ApiParam(value="생성할 API사용자 ID, PW")
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

    @ApiOperation(value = "등록된 API사용자의 JWT 토큰 발급")
    @ApiResponses({
            @ApiResponse(code = 200, message = "사용자ID을 본문으로 응답. 응답헤더의 `Token`으 JWT토큰을 넣어 응답"),
            @ApiResponse(code = 400, message = "사용자ID/Password 틀림")
    })
    @RequestMapping(path = "/signin",
            method = POST,
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> signIn(
            @ApiParam(value="로그인할 API사용자 ID, PW")
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

    @ApiOperation(value = "JWT토큰 갱신 (`Authorization` 헤더 인증 필요)")
    @ApiResponses({
            @ApiResponse(code = 200, message = "응답헤더의 `Token`으 JWT토큰을 넣어 응답"),
    })
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
