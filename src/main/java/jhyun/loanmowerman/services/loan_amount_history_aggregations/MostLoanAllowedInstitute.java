package jhyun.loanmowerman.services.loan_amount_history_aggregations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@ApiModel("특정연도의 가장 지원금액이 큰 은행")
@Builder
@Data
public class MostLoanAllowedInstitute implements Serializable {
    @ApiModelProperty(required = true, value = "해당연도")
    private Integer year;

    @ApiModelProperty(required = true, value = "은행이름")
    @JsonProperty("bank")
    private String instituteName;
}
