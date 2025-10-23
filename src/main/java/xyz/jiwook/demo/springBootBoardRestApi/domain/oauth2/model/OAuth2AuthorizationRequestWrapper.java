package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.io.Serializable;

public record OAuth2AuthorizationRequestWrapper(OAuth2AuthorizationRequest authorizationRequest, String purpose) implements Serializable {

}
