package jhyun.loanmowerman.controllers.predictions;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@EqualsAndHashCode
@ToString
public class LoanAmountPredictionOfMonthRequest {

    @Getter
    @Setter
    @ApiModelProperty(required = true)
    private Integer month;

    @Getter
    @Setter
    @ApiModelProperty(required = true, value = "은행이름")
    private String bank;

    public LoanAmountPredictionOfMonthRequest() {
    }

    public LoanAmountPredictionOfMonthRequest(Integer month, String bank) {
        this.month = month;
        this.bank = bank;
    }
}
