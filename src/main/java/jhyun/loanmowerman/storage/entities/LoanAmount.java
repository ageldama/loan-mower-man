package jhyun.loanmowerman.storage.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@ToString
@EqualsAndHashCode
@Entity
@Table(name = "loan_amounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"institute_code", "year", "month"})
})
public class LoanAmount implements Serializable {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "loan_amounts_id_seq")
    @Column(name = "id")
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "institute_code",
            referencedColumnName = "institute_code")
    private Institute institute;

    @Getter
    @Setter
    @Column(name = "year")
    private Integer year;

    @Getter
    @Setter
    @Column(name = "month")
    private Integer month;

    @Getter
    @Setter
    @Column(name = "amount")
    private Integer amount;

    public LoanAmount() {
    }

    public LoanAmount(Institute institute, Integer year, Integer month, Integer amount) {
        this.institute = institute;
        this.year = year;
        this.month = month;
        this.amount = amount;
    }
}
