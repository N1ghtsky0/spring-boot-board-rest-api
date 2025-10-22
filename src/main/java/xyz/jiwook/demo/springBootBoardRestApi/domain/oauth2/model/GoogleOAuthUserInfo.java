package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model;

import java.util.Map;

public class GoogleOAuthUserInfo extends OAuthUserInfo {
    public GoogleOAuthUserInfo(Map<String, Object> attributes) {
        super("google", attributes);
        super.setProviderId((String) attributes.get("sub"));
        super.setEmail((String) attributes.get("email"));
    }
}
