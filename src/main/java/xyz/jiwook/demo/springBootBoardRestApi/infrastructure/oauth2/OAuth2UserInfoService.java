package xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.dto.OAuth2UserInfoDto;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider.GoogleOAuth2UserInfo;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider.KakaoOAuth2UserInfo;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider.NaverOAuth2UserInfo;
import xyz.jiwook.demo.springBootBoardRestApi.infrastructure.oauth2.provider.OAuth2UserInfo;

@Service
public class OAuth2UserInfoService {

    public OAuth2UserInfoDto extractUserInfo(String registrationId, OAuth2User oauth2User) {
        OAuth2UserInfo userInfo = switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(oauth2User.getAttributes());
            case "kakao" -> new KakaoOAuth2UserInfo(oauth2User.getAttributes());
            case "naver" -> new NaverOAuth2UserInfo(oauth2User.getAttributes());
            default -> throw new BusinessException("Unsupported OAuth2 provider: " + registrationId);
        };

        return new OAuth2UserInfoDto(
                userInfo.getProviderName(),
                userInfo.getProviderId(),
                userInfo.getEmail()
        );
    }
}
