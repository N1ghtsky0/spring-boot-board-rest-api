package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model;

import java.util.Map;

public class KakaoOAuthUserInfo extends OAuthUserInfo {
    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        super("kakao", attributes);

        super.setProviderId((String) attributes.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        super.setEmail((String) kakaoAccount.get("email"));
    }
}
