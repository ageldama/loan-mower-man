package jhyun.loanmowerman.controllers;

import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.controllers.api_user.ApiUserDuplicatedException;
import jhyun.loanmowerman.services.predictions.NoSuchPredictorStrategyException;
import jhyun.loanmowerman.services.predictions.PredictionNotPreparedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionAdvices {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiUserDuplicatedException.class)
    public void apiUserDuplicated() {}

    @ResponseStatus(HttpStatus.FAILED_DEPENDENCY)
    @ExceptionHandler(NoDataException.class)
    public void NoData() {}

    @ResponseStatus(HttpStatus.DESTINATION_LOCKED)
    @ExceptionHandler(PredictionNotPreparedException.class)
    public void predictionNotPrepared() {}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchPredictorStrategyException.class)
    public void NoSuchPredictorStrategy() {}
}
