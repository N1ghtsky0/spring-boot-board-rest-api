package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.dto.OAuth2AuthorizationRequestDto;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class OAuth2StateRepository {
    private static final String KEY_PREFIX = "oauth2:authorization:state:";
    private static final long EXPIRATION_MINUTES = 5;

    private final RedisTemplate<Object, Object> redisTemplate;

    public void save(String state, OAuth2AuthorizationRequestDto authorizationRequest) {
        String key = KEY_PREFIX + state;
        redisTemplate.opsForValue().set(key, authorizationRequest, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    public OAuth2AuthorizationRequestDto findAndRemove(String state) {
        String key = KEY_PREFIX + state;
        OAuth2AuthorizationRequestDto value = (OAuth2AuthorizationRequestDto) redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        return value;
    }
}
