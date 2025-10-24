package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super("google", attributes);
        super.setProviderId((String) attributes.get("sub"));
        super.setEmail((String) attributes.get("email"));
    }
}
