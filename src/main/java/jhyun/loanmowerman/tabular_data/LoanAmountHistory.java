package jhyun.loanmowerman.tabular_data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@EqualsAndHashCode
public class LoanAmountHistory {
    @Getter
    private Integer year;

    @Getter
    private Integer month;

    @Getter
    private Map<InstituteIndexAndName, Integer> amountsPerInstitute;

    public LoanAmountHistory(Integer year, Integer month,
                             Map<InstituteIndexAndName, Integer> amountsPerInstitute) {
        this.year = year;
        this.month = month;
        this.amountsPerInstitute = amountsPerInstitute;
    }
}
