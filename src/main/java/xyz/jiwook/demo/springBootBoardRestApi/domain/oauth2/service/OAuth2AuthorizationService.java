package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;

@Service
public class OAuth2AuthorizationService {
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizationRequestResolver authorizationRequestResolver;

    public OAuth2AuthorizationService(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
    }

    public String getAuthorizationUri(HttpServletRequest request, String registrationId) {
        return this.resolveAuthorizationRequest(request, registrationId).getAuthorizationRequestUri();
    }

    private OAuth2AuthorizationRequest resolveAuthorizationRequest(HttpServletRequest request, String registrationId) {
        if (clientRegistrationRepository.findByRegistrationId(registrationId) == null) {
            throw new BusinessException("Invalid Client Registration Id: " + registrationId);
        }
        return authorizationRequestResolver.resolve(request, registrationId);
    }
}
