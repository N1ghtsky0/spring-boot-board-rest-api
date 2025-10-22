package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model;

import java.util.Map;

public class NaverOAuthUserInfo extends OAuthUserInfo {
    public NaverOAuthUserInfo(Map<String, Object> attributes) {
        super("naver", attributes);

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        super.setProviderId((String) response.get("id"));
        super.setEmail((String) response.get("email"));
    }
}
