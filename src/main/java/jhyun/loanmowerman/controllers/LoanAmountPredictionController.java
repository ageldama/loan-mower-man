package jhyun.loanmowerman.controllers;

import com.google.common.collect.Lists;
import io.swagger.annotations.*;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.controllers.predictions.LoanAmountPredictionOfMonthRequest;
import jhyun.loanmowerman.services.LoanAmountPredictionService;
import jhyun.loanmowerman.services.predictions.LoanAmountPrediction;
import jhyun.loanmowerman.services.predictions.NoSuchPredictorStrategyException;
import jhyun.loanmowerman.services.predictions.PredictionNotPreparedException;
import jhyun.loanmowerman.storage.repositories.InstituteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "지원금액 추정")
@RequestMapping(path = "/predictions")
@RestController
public class LoanAmountPredictionController {

    private LoanAmountPredictionService loanAmountPredictionService;
    private InstituteRepository instituteRepository;

    @Autowired
    public LoanAmountPredictionController(
            LoanAmountPredictionService loanAmountPredictionService,
            InstituteRepository instituteRepository
    ) {
        this.loanAmountPredictionService = loanAmountPredictionService;
        this.instituteRepository = instituteRepository;
    }

    @ApiOperation(value = "지원금액 추정")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 421, message = "지정한 전략으로 추정하기 위한 준비가 아직 완료되기 전"),
            @ApiResponse(code = 424, message = "지정한 은행이름으로 은행을 찾을 수 없음 or 추정을 위한 데이터 없음")
    })
    @RequestMapping(path = "/", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public LoanAmountPrediction predict(
            @RequestBody
                    LoanAmountPredictionOfMonthRequest request,
            @ApiParam(
                    value = "추정에 사용할 전략의 코드이름",
                    defaultValue = "linear_regression",
                    allowableValues = "average, average_all, linear_regression"
            )
            @RequestParam(required = false, name = "strategy", defaultValue = "linear_regression") String strategy
    ) throws NoDataException, NoSuchPredictorStrategyException, PredictionNotPreparedException {
        final int year = 2018; // 일단 고정
        final List<String> instituteCodes =
                Lists.newArrayList(instituteRepository.findInstituteCodeByName(request.getBank()));
        if (instituteCodes.size() != 1) {
            throw new NoDataException(request.getBank());
        }
        final String instituteCode = instituteCodes.get(0);
        return loanAmountPredictionService.predict(strategy, year, request.getMonth(), instituteCode);
    }
}
