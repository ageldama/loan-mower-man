package jhyun.loanmowerman.services.loan_amount_history_aggregations;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@ApiModel("연도별 각 금융기관의 지원금액 합계")
@Builder
@Data
public class TotalLoanAmounts {
    @ApiModelProperty(required = true)
    private String name = "주택금융 공급현황";

    @ApiModelProperty(value = "연도별 공급현황")
    private List<TotalLoanAmountEntry> entries;
}
