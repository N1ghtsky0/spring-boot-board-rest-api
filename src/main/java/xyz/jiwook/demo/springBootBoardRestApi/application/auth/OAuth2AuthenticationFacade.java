package xyz.jiwook.demo.springBootBoardRestApi.application.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.dto.OAuth2UserInfoDto;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.usecase.OAuth2JoinUseCase;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.usecase.OAuth2LoginUseCase;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.jwt.JwtTokenProvider;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.OAuth2AuthenticationService;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.dto.OAuth2AuthorizationRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2AuthenticationFacade {
    private final OAuth2AuthenticationService oauth2AuthenticationService;
    private final OAuth2LoginUseCase oauth2LoginUseCase;
    private final OAuth2JoinUseCase oauth2JoinUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public String getAuthorizationUri(HttpServletRequest request, String registrationId, String purpose) {
        return oauth2AuthenticationService.createAuthorizationUri(request, registrationId, purpose);
    }

    public void handleCallback(HttpServletRequest request, HttpServletResponse response, String registrationId) {
        MultiValueMap<String, String> params = OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        if (!OAuth2AuthorizationResponseUtils.isAuthorizationResponse(params)) {
            throw new BusinessException("Invalid authorization response");
        }

        OAuth2AuthorizationRequestDto authorizationRequestDto =
                oauth2AuthenticationService.getAuthorizationRequestFromState(params.getFirst("state"));

        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestDto.authorizationRequest();

        String redirectUri = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request))
                .replaceQuery(null)
                .build()
                .toUriString();

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponseUtils.convert(params, redirectUri);

        OAuth2UserInfoDto userInfo = oauth2AuthenticationService.processCallback(
                authorizationRequest,
                authorizationResponse,
                registrationId
        );

        String purpose = authorizationRequestDto.purpose();

        switch (purpose) {
            case "login" -> handleLogin(response, userInfo);
            case "join" -> handleJoin(userInfo);
            case "connect" -> handleConnect(userInfo);
            default -> throw new BusinessException("Invalid purpose: " + purpose);
        }
    }

    private void handleLogin(HttpServletResponse response, OAuth2UserInfoDto userInfo) {
        String sub = oauth2LoginUseCase.execute(userInfo);
        String accessToken = jwtTokenProvider.generateAccessToken(sub);
        response.addHeader("Authorization", accessToken);
    }

    private void handleJoin(OAuth2UserInfoDto userInfo) {
        oauth2JoinUseCase.execute(userInfo);
    }

    private void handleConnect(OAuth2UserInfoDto userInfo) {
        log.info("새 소셜계정 연결 진행: {}", userInfo.providerName());
        // TODO: 소셜계정 연결 로직 구현
    }
}
