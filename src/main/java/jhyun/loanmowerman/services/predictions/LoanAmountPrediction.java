package jhyun.loanmowerman.services.predictions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ApiModel("지원 금액 예측결과")
@EqualsAndHashCode
@ToString
@Builder
public class LoanAmountPrediction {

    @ApiModelProperty(value = "은행코드")
    private String bank;

    private Integer year;

    private Integer month;

    private Long amount;

    public LoanAmountPrediction() {
    }

    public LoanAmountPrediction(String bank, Integer year, Integer month, Long amount) {
        this.bank = bank;
        this.year = year;
        this.month = month;
        this.amount = amount;
    }
}
