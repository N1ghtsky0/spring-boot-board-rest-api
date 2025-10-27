package xyz.jiwook.demo.springBootBoardRestApi.presentation.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.OAuth2AuthenticationFacade;
import xyz.jiwook.demo.springBootBoardRestApi.global.common.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final OAuth2AuthenticationFacade authenticationFacade;

    @GetMapping("/oauth2/authorization/{registrationId}/join")
    public ResponseEntity<ApiResponse> getAuthorizationUriForJoin(
            @PathVariable String registrationId,
            HttpServletRequest request
    ) {
        String authorizationUri = authenticationFacade.getAuthorizationUri(request, registrationId, "join");
        return ResponseEntity.ok(ApiResponse.success(Map.of("authorizationUri", authorizationUri)));
    }

    @GetMapping("/oauth2/authorization/{registrationId}/login")
    public ResponseEntity<ApiResponse> getAuthorizationUriForLogin(
            @PathVariable String registrationId,
            HttpServletRequest request
    ) {
        String authorizationUri = authenticationFacade.getAuthorizationUri(request, registrationId, "login");
        return ResponseEntity.ok(ApiResponse.success(Map.of("authorizationUri", authorizationUri)));
    }

    @GetMapping("/oauth2/authorization/{registrationId}/connect")
    public ResponseEntity<ApiResponse> getAuthorizationUriForConnect(
            @PathVariable String registrationId,
            HttpServletRequest request
    ) {
        String authorizationUri = authenticationFacade.getAuthorizationUri(request, registrationId, "connect");
        return ResponseEntity.ok(ApiResponse.success(Map.of("authorizationUri", authorizationUri)));
    }

    @GetMapping("/oauth2/callback/{registrationId}")
    public ResponseEntity<ApiResponse> handleOAuth2Callback(
            @PathVariable String registrationId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authenticationFacade.handleCallback(request, response, registrationId);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "Authentication successful")));
    }
}
