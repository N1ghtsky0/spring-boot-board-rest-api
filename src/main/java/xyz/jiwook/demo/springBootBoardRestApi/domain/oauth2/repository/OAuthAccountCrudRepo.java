package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.repository;

import org.springframework.data.repository.CrudRepository;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.OAuthAccountEntity;

import java.util.Optional;

public interface OAuthAccountCrudRepo extends CrudRepository<OAuthAccountEntity, Long> {
    Optional<OAuthAccountEntity> findByProviderNameAndProviderId(String providerName, String providerId);
}
