package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class OAuth2UserInfo {
    protected final String providerName;
    protected final Map<String, Object> attributes;

    protected String providerId;
    protected String email;

    protected OAuth2UserInfo(String providerName, Map<String, Object> attributes) {
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
