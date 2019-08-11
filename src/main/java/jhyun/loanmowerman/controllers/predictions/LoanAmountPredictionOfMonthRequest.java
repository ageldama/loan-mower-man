package jhyun.loanmowerman.controllers.predictions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
public class LoanAmountPredictionOfMonthRequest {

    @ApiModelProperty(required = true)
    private Integer month;

    @ApiModelProperty(required = true, value = "은행이름")
    private String bank;

    public LoanAmountPredictionOfMonthRequest() {
    }

    public LoanAmountPredictionOfMonthRequest(Integer month, String bank) {
        this.month = month;
        this.bank = bank;
    }
}
