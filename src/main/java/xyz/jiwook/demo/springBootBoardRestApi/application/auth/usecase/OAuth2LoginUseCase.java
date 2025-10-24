package xyz.jiwook.demo.springBootBoardRestApi.application.auth.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.dto.OAuth2UserInfoDto;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.Member;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount.OAuthAccount;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount.OAuthAccountService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuth2LoginUseCase {
    private final OAuthAccountService oauthAccountService;

    public String execute(OAuth2UserInfoDto userInfo) {
        OAuthAccount oauthAccount = oauthAccountService.findOAuthAccount(
                userInfo.providerName(),
                userInfo.providerId()
        );
        Member member = oauthAccount.getMember();
        return member.getSub();
    }
}
