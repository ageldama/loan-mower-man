package jhyun.loanmowerman.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jhyun.loanmowerman.storage.entities.ApiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneOffset.UTC;

@Component
public class JwtService {
    // @Value("${jwt.token.clientSecret}")
    private String clientSecret = "secretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecretsecret";

    // @Value("${jwt.token.issuer}")
    private String issuer = "ageldama@gmail.com";

    // @Value("${jwt.token.expirySeconds}")
    private Long expirySeconds = 7200L;

    @Autowired
    public JwtService() {
    }

    public String generateToken(ApiUser apiUser) throws IOException, URISyntaxException {
        final byte[] secretKey = clientSecret.getBytes();
        final Date expiration = Date.from(LocalDateTime.now().plusSeconds(expirySeconds).toInstant(UTC));
        return Jwts.builder()
                .setSubject(apiUser.getId())
                .setExpiration(expiration)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }
}
