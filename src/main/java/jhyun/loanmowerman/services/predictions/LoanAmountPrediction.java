package jhyun.loanmowerman.services.predictions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ApiModel("지원 금액 예측결과")
@EqualsAndHashCode
@ToString
@Builder
public class LoanAmountPrediction {

    @Getter
    @Setter
    @ApiModelProperty(required = true, value = "은행코드")
    private String bank;

    @Getter
    @Setter
    private Integer year;

    @Getter
    @Setter
    private Integer month;

    @ApiModelProperty(required = true, value="금액(억KRW)")
    @Getter
    @Setter
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
