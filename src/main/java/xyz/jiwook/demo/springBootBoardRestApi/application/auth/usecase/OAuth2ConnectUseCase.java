package xyz.jiwook.demo.springBootBoardRestApi.application.auth.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.jiwook.demo.springBootBoardRestApi.application.auth.dto.OAuth2UserInfoDto;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.Member;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.MemberService;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount.OAuthAccountService;

@Service
@RequiredArgsConstructor
public class OAuth2ConnectUseCase {
    private final MemberService memberService;
    private final OAuthAccountService oauthAccountService;

    @Transactional
    public void execute(OAuth2UserInfoDto userInfo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberService.getMemberBySub(authentication.getName());

        oauthAccountService.linkOAuthAccount(
                userInfo.providerName(),
                userInfo.providerId(),
                member
        );
    }
}
