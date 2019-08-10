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
@Table(name = "api_users")
public class ApiUser implements Serializable {
    @Getter
    @Setter
    @Id
    @Column(name = "id")
    private String id;

    @Getter
    @Setter
    @Column(name = "password")
    private String password;

    public ApiUser() {
    }

    public ApiUser(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
