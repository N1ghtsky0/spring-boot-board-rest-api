package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class OAuthUserInfo {
    private final String providerName;
    private final Map<String, Object> attributes;

    private String providerId;
    private String email;

    public OAuthUserInfo(String providerName, Map<String, Object> attributes) {
        this.providerName = providerName;
        this.attributes = attributes;
    }

    protected void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    protected void setEmail(String email) {
        this.email = email;
    }
}
