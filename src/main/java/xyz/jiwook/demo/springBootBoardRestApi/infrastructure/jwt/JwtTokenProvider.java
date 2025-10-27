package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    private final String secretKey;
    private final long accessTokenValidityMillis = 1000 * 60 * 30; // 30분

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateAccessToken(String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(now))
                .expiration(new Date(now + accessTokenValidityMillis))
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubjectFromJwtToken(final String token) {
        return this.extractFromToken(token, Claims::getSubject);
    }

    private <T> T extractFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = Jwts.parser().verifyWith((SecretKey) getSigningKey()).build().parseSignedClaims(token).getPayload();
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            return claimsResolver.apply(e.getClaims());
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
