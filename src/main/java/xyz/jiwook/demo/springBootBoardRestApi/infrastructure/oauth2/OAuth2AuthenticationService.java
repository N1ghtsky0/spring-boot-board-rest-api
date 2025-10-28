package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.dto.OAuth2UserInfoDto;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.dto.OAuth2AuthorizationRequestDto;

@Service
public class OAuth2AuthenticationService {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> tokenResponseClient;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;
    private final OAuth2StateRepository stateRepository;
    private final OAuth2UserInfoService userInfoService;

    public OAuth2AuthenticationService(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2StateRepository stateRepository,
            OAuth2UserInfoService userInfoService
    ) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
        );
        this.tokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
        this.oauth2UserService = new DefaultOAuth2UserService();
        this.stateRepository = stateRepository;
        this.userInfoService = userInfoService;
    }

    public String createAuthorizationUri(HttpServletRequest request, String registrationId, String purpose) {
        validateRegistrationId(registrationId);

        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestResolver.resolve(request, registrationId);
        if (authorizationRequest == null) {
            throw new BusinessException(OAuth2ErrorCode.BAD_PROVIDER);
        }

        OAuth2AuthorizationRequestDto requestDto = new OAuth2AuthorizationRequestDto(authorizationRequest, purpose);
        stateRepository.save(authorizationRequest.getState(), requestDto);

        return authorizationRequest.getAuthorizationRequestUri();
    }

    public OAuth2AuthorizationRequestDto getAuthorizationRequestFromState(String state) {
        OAuth2AuthorizationRequestDto requestDto = stateRepository.findAndRemove(state);
        if (requestDto == null) {
            throw new BusinessException(OAuth2ErrorCode.INVALID_STATE);
        }
        return requestDto;
    }

    public OAuth2UserInfoDto processCallback(
            OAuth2AuthorizationRequest authorizationRequest,
            OAuth2AuthorizationResponse authorizationResponse,
            String registrationId
    ) {

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(
                authorizationRequest,
                authorizationResponse
        );

        OAuth2AuthorizationCodeGrantRequest tokenRequest = new OAuth2AuthorizationCodeGrantRequest(
                clientRegistration,
                exchange
        );

        OAuth2AccessTokenResponse tokenResponse = tokenResponseClient.getTokenResponse(tokenRequest);
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());
        OAuth2User oauth2User = oauth2UserService.loadUser(userRequest);

        return userInfoService.extractUserInfo(registrationId, oauth2User);
    }

    private void validateRegistrationId(String registrationId) {
        if (clientRegistrationRepository.findByRegistrationId(registrationId) == null) {
            throw new BusinessException(OAuth2ErrorCode.BAD_PROVIDER);
        }
    }
}
