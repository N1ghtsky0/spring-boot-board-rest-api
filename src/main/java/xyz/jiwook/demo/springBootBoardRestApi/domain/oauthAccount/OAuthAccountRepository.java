package xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends CrudRepository<OAuthAccount, Long> {
    Optional<OAuthAccount> findByProviderNameAndProviderId(String providerName, String providerId);

    boolean existsByProviderNameAndProviderId(String providerName, String providerId);
}
