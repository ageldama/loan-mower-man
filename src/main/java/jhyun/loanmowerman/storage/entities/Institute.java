package jhyun.loanmowerman.storage.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@ToString
@EqualsAndHashCode
@Entity
@Table(name = "institutes")
public class Institute implements Serializable {

    @Getter
    @Setter
    @Id
    @Column(name = "institute_code")
    private String code;

    @Getter
    @Setter
    @Id
    @Column(name = "institute_name")
    private String name;

    public Institute() {
    }

    public Institute(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
