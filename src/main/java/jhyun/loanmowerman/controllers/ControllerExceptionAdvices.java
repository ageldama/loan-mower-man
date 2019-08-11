package jhyun.loanmowerman.controllers;

import jhyun.loanmowerman.controllers.api_user.ApiUserDuplicatedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionAdvices {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiUserDuplicatedException.class)
    public void apiUserDuplicated() {}
}
