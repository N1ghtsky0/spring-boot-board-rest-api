package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super("kakao", attributes);
        super.setProviderId(String.valueOf(attributes.get("id")));

        @SuppressWarnings("unchecked")
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        super.setEmail((String) kakaoAccount.get("email"));
    }
}
