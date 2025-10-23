package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Repository
public class OAuthRedisRepo {
    private final RedisTemplate<Object, Object> redisTemplate;

    private final String OAUTH2_AUTHORIZATION_REQUEST_PREFIX = "oauth2:authorization:request:state:";

    public void saveOAuth2AuthorizationRequest(String key, OAuth2AuthorizationRequest value) {
        redisTemplate.opsForValue().set(OAUTH2_AUTHORIZATION_REQUEST_PREFIX + key, value, 1, TimeUnit.MINUTES);
    }

    public OAuth2AuthorizationRequest removeOAuth2AuthorizationRequest(String key) {
        OAuth2AuthorizationRequest value = (OAuth2AuthorizationRequest) redisTemplate.opsForValue().get(OAUTH2_AUTHORIZATION_REQUEST_PREFIX + key);
        redisTemplate.delete(OAUTH2_AUTHORIZATION_REQUEST_PREFIX + key);
        return value;
    }
}
