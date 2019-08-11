package jhyun.loanmowerman.services.loan_amount_history_aggregations;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("연도와 금액")
@Data
@Builder
public class YearAndAmountEntry {
    @ApiModelProperty(required = true, value = "연도")
    private Integer year;

    @ApiModelProperty(required = true, value = "금액")
    private Long amount;
}
