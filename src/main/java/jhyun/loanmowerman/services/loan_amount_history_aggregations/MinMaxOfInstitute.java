package jhyun.loanmowerman.services.loan_amount_history_aggregations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@ApiModel("특정연도-기관의 지원금액 최소-최대")
@Builder
@Data
public class MinMaxOfInstitute {

    @ApiModelProperty(required = true, value="은행이름")
    @JsonProperty("bank")
    private String instituteName;

    @ApiModelProperty(required = true, value = "지원금액 최소와 최대")
    @JsonProperty("support_amount")
    private List<YearAndAmountEntry> minMax;
}
