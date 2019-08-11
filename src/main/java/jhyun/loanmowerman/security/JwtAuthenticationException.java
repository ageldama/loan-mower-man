package jhyun.loanmowerman.security;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String reason, Exception e) {
        super(reason, e);
    }
}
