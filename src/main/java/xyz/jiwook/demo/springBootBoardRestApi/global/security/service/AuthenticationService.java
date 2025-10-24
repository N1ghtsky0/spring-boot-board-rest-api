package xyz.jiwook.demo.springBootBoardRestApi.global.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.model.MemberEntity;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.OAuthAccountEntity;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.OAuthUserInfo;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.repository.OAuthAccountCrudRepo;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final OAuthAccountCrudRepo oAuthAccountRepo;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public void loginProcess(HttpServletResponse response, OAuthUserInfo loginUserInfo) {
        OAuthAccountEntity oAuthAccountEntity = oAuthAccountRepo
                .findByProviderNameAndProviderId(loginUserInfo.getProviderName(), loginUserInfo.getProviderId())
                .orElseThrow(() -> new IllegalStateException("This account is not registered."));

        MemberEntity memberEntity = oAuthAccountEntity.getMember();

        final long now = System.currentTimeMillis();
        String accessToken = Jwts.builder()
                .subject(memberEntity.getSub())
                .issuedAt(new Date(now))
                .expiration(new Date(now + 1000 * 60 * 30))
                .signWith(getSecretKey())
                .compact();

        response.addHeader("Authorization", accessToken);
    }

    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
