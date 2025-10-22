package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.service;

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
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;

@Service
public class OAuth2AuthorizationService {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

    public OAuth2AuthorizationService(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    public String getAuthorizationUri(HttpServletRequest request, String registrationId) {
        return this.resolveAuthorizationRequest(request, registrationId).getAuthorizationRequestUri();
    }

    public void attemptAuthentication(HttpServletRequest request, String registrationId, String code) {
        OAuth2AuthorizationRequest authorizationRequest = this.resolveAuthorizationRequest(request, registrationId);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(code)
                .redirectUri(authorizationRequest.getRedirectUri())
                .state(authorizationRequest.getState())
                .build();

        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        OAuth2AuthorizationCodeGrantRequest tokenRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, exchange);
        OAuth2AccessTokenResponse tokenResponse = accessTokenResponseClient.getTokenResponse(tokenRequest);
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());
        OAuth2User user = oAuth2UserService.loadUser(userRequest);
        //todo 유저정보 조회 성공 시 엑세스-리프레시 토큰 발급

    }

    private OAuth2AuthorizationRequest resolveAuthorizationRequest(HttpServletRequest request, String registrationId) {
        if (clientRegistrationRepository.findByRegistrationId(registrationId) == null) {
            throw new BusinessException("Invalid Client Registration Id: " + registrationId);
        }
        return authorizationRequestResolver.resolve(request, registrationId);
    }
}
