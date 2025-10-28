package xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.Member;
import xyz.jiwook.demo.springBootBoardRestApi.global.exception.BusinessException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OAuthAccountService {
    private final OAuthAccountRepository oauthAccountRepository;

    @Transactional
    public void linkOAuthAccount(String providerName, String providerId, Member member) {
        validateNotDuplicated(providerName, providerId);
        oauthAccountRepository.save(new OAuthAccount(providerName, providerId, member));
    }

    public OAuthAccount findOAuthAccount(String providerName, String providerId) {
        return oauthAccountRepository.findByProviderNameAndProviderId(providerName, providerId)
                .orElseThrow(() -> new BusinessException(OAuthAccountErrorCode.NOT_FOUND));
    }

    public boolean isAccountRegistered(String providerName, String providerId) {
        return oauthAccountRepository.existsByProviderNameAndProviderId(providerName, providerId);
    }

    private void validateNotDuplicated(String providerName, String providerId) {
        if (isAccountRegistered(providerName, providerId)) {
            throw new BusinessException(OAuthAccountErrorCode.ALREADY_REGISTERED);
        }
    }
}
