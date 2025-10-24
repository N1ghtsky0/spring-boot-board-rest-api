package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super("naver", attributes);

        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        super.setProviderId((String) response.get("id"));
        super.setEmail((String) response.get("email"));
    }
}
