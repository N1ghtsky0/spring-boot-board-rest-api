package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.service.MemberService;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.*;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.repository.OAuthRedisRepo;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;
import xyz.jiwook.demo.springBootBoardRestApi.global.security.service.AuthenticationService;

import java.util.Map;

@Slf4j
@Service
public class OAuth2AuthorizationService {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient = new RestClientAuthorizationCodeTokenResponseClient();
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
    private final OAuthRedisRepo oAuthRedisRepo;
    private final MemberService memberService;
    private final AuthenticationService authenticationService;

    public OAuth2AuthorizationService(ClientRegistrationRepository clientRegistrationRepository, OAuthRedisRepo oAuthRedisRepo,
                                      MemberService memberService, AuthenticationService authenticationService) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
        this.oAuthRedisRepo = oAuthRedisRepo;
        this.memberService = memberService;
        this.authenticationService = authenticationService;
    }

    public String getAuthorizationUri(HttpServletRequest request, String registrationId, String purpose) {
        OAuth2AuthorizationRequest authorizationRequest = this.resolveAuthorizationRequest(request, registrationId);
        oAuthRedisRepo.saveOAuth2AuthorizationRequest(authorizationRequest.getState(), new OAuth2AuthorizationRequestWrapper(authorizationRequest, purpose));
        return authorizationRequest.getAuthorizationRequestUri();
    }

    public void attemptAuthentication(HttpServletRequest request, HttpServletResponse response, String registrationId) {
        Map<String, String[]> requestParamMap = request.getParameterMap();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        requestParamMap.forEach((key, values) -> {
            for (String value: values) {
                params.add(key, value);
            }
        });

        OAuth2AuthorizationRequestWrapper authorizationRequestWrapper = oAuthRedisRepo.removeOAuth2AuthorizationRequest(params.getFirst("state"));
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestWrapper.authorizationRequest();

        if (authorizationRequest == null) {
            throw new BusinessException("Invalid state parameter.");
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponse.success(params.getFirst("code"))
                .redirectUri(authorizationRequest.getRedirectUri())
                .state(authorizationRequest.getState())
                .build();

        OAuth2AuthorizationExchange exchange = new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse);
        OAuth2AuthorizationCodeGrantRequest tokenRequest = new OAuth2AuthorizationCodeGrantRequest(clientRegistration, exchange);
        OAuth2AccessTokenResponse tokenResponse = accessTokenResponseClient.getTokenResponse(tokenRequest);
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, tokenResponse.getAccessToken());
        OAuth2User user = oAuth2UserService.loadUser(userRequest);

        OAuthUserInfo oAuthUserInfo = this.generateOAuthUserInfo(registrationId, user);

        if (oAuthUserInfo == null) {
            throw new BusinessException("Invalid Client Registration Id: " + registrationId);
        }

        final String purpose = authorizationRequestWrapper.purpose();
        switch (purpose) {
            case "login" -> authenticationService.loginProcess(response, oAuthUserInfo);
            case "join" -> memberService.join(oAuthUserInfo);
            case "connect" -> log.info("새 소셜계정연결 진행");
            case null, default -> throw new BusinessException("Invalid purpose parameter.");
        }
    }

    private OAuth2AuthorizationRequest resolveAuthorizationRequest(HttpServletRequest request, String registrationId) {
        if (clientRegistrationRepository.findByRegistrationId(registrationId) == null) {
            throw new BusinessException("Invalid Client Registration Id: " + registrationId);
        }
        return authorizationRequestResolver.resolve(request, registrationId);
    }

    private OAuthUserInfo generateOAuthUserInfo(String registrationId, OAuth2User oAuth2User) {
        if ("google".equals(registrationId)) {
            return new GoogleOAuthUserInfo(oAuth2User.getAttributes());
        } else if ("naver".equals(registrationId)) {
            return new NaverOAuthUserInfo(oAuth2User.getAttributes());
        } else if ("kakao".equals(registrationId)) {
            return new KakaoOAuthUserInfo(oAuth2User.getAttributes());
        }
        return null;
    }
}
