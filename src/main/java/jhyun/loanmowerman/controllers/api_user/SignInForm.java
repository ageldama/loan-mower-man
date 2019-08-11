package jhyun.loanmowerman.controllers.api_user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("로그인 정보 ID, PW")
@Data
public class SignInForm implements Serializable {

    @ApiModelProperty(required = true, value = "사용자ID")
    private String id;

    @ApiModelProperty(required = true, value = "사용자 PW (cleartext)")
    private String password;

    public SignInForm() {
    }

    public SignInForm(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
