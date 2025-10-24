package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.dto;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.io.Serializable;

public record OAuth2AuthorizationRequestDto(
        OAuth2AuthorizationRequest authorizationRequest,
        String purpose
) implements Serializable {
}
