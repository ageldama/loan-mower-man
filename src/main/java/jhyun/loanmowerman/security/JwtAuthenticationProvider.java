package jhyun.loanmowerman.security;

import jhyun.loanmowerman.services.JwtService;
import jhyun.loanmowerman.storage.entities.ApiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private JwtService jwtService;

    @Autowired
    public JwtAuthenticationProvider(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            final Optional<ApiUser> apiUser = jwtService.verify((String) authentication.getCredentials());
            if (!apiUser.isPresent()) {
                throw new JwtAuthenticationException("Failed to verify token", null);
            }
            return new JwtAuthenticatedProfile(apiUser.get());
        } catch (Exception e) {
            throw new JwtAuthenticationException("Failed to verify token", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthToken.class.equals(authentication);
    }
}
