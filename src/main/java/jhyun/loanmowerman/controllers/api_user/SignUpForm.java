package jhyun.loanmowerman.controllers.api_user;

import lombok.Data;

import java.io.Serializable;

@Data
public class SignUpForm implements Serializable {
    private String id;
    private String password;
}
