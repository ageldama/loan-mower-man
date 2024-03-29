package jhyun.loanmowerman.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jhyun.loanmowerman.controllers.aggregations.NoDataException;
import jhyun.loanmowerman.services.LoanAmountHistoryAggregationService;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.MinMaxOfInstitute;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.MostLoanAllowedInstitute;
import jhyun.loanmowerman.services.loan_amount_history_aggregations.TotalLoanAmounts;
import jhyun.loanmowerman.storage.entities.Institute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Api(description = "입력된 은행목록, 금액지원 내역의 통계/합산")
@RequestMapping(path = "/aggregations")
@RestController
public class LoanAmountHistoryAggregationController {

    private LoanAmountHistoryAggregationService loanAmountHistoryAggregationService;

    @Autowired
    public LoanAmountHistoryAggregationController(LoanAmountHistoryAggregationService loanAmountHistoryAggregationService) {
        this.loanAmountHistoryAggregationService = loanAmountHistoryAggregationService;
    }

    @ApiOperation(value = "특정연도의 가장 지원금액 큰 은행")
    @RequestMapping(path = "/mostOf/{year}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MostLoanAllowedInstitute mostLoanAllowedInstitute(
            @PathVariable("year") final Integer year
    ) throws NoDataException {
        Optional<Institute> result = loanAmountHistoryAggregationService.mostLoanAllowedInstitute(year);
        if (result.isPresent()) {
            return  MostLoanAllowedInstitute.builder()
                    .year(year).instituteName(result.get().getName())
                    .build();
        } else {
            throw new NoDataException("Data cannot be found");
        }
    }

    @ApiOperation(value = "연도별 각 금융기관의 지원금액 합계")
    @RequestMapping(path = "/totalByYears", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TotalLoanAmounts totalLoanAmountsByYear() {
        return loanAmountHistoryAggregationService.totalLoanAmountsByYear();
    }

    @ApiOperation(value = "외환은행의 지급액 평균(연도별) 최소/최대값")
    @RequestMapping(path = "/oehwaneunhaeng", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MinMaxOfInstitute oehwaneunhaeng() {
        return loanAmountHistoryAggregationService.findMinMaxOfInstitute("외환은행");
    }
}
