package jhyun.loanmowerman.tabular_data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class InstituteIndexAndName {

    @Getter
    private Integer index;

    @Getter
    private String name;

    public InstituteIndexAndName(Integer index, String name) {
        this.index = index;
        this.name = name;
    }
}
