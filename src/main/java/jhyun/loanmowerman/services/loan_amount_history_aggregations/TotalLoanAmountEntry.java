package jhyun.loanmowerman.services.loan_amount_history_aggregations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@ApiModel("지원현황 각 연도 항목")
public class TotalLoanAmountEntry {

    @JsonProperty("detail_amount")
    @ApiModelProperty(required = true, value = "은행별 금액")
    private Map<String, Long> amounts;

    @JsonProperty("total_amount")
    @ApiModelProperty(required = true, value="총합")
    private Long total;

    @ApiModelProperty(required = true)
    private String year;
}
