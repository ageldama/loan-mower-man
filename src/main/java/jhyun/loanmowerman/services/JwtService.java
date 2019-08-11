package jhyun.loanmowerman.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jhyun.loanmowerman.storage.entities.ApiUser;
import jhyun.loanmowerman.storage.repositories.ApiUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Component
public class JwtService {
    // @Value("${jwt.token.clientSecret}")
    private String clientSecret = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret";

    // @Value("${jwt.token.issuer}")
    private String issuer = "ageldama@gmail.com";

    // @Value("${jwt.token.expirySeconds}")
    private Long expirySeconds = 7200L;

    private ApiUserRepository apiUserRepository;

    @Autowired
    public JwtService(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

    protected byte[] getSecretKey() {
        return clientSecret.getBytes();
    }

    public String generateToken(ApiUser apiUser) throws IOException, URISyntaxException {
        final Date expiration = Date.from(LocalDateTime.now().plusSeconds(expirySeconds).toInstant(UTC));
        return Jwts.builder()
                .setSubject(apiUser.getId())
                .setExpiration(expiration)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS512, getSecretKey())
                .compact();
    }

    public Optional<ApiUser> verify(final String token) throws IOException, URISyntaxException {
        final Jws<Claims> claims = Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token);
        final String apiUserId = claims.getBody().getSubject();
        return apiUserRepository.findById(apiUserId);
    }
}
