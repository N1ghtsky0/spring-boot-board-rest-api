package xyz.jiwook.demo.springBootBoardRestApi.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.model.MemberEntity;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.repo.MemberCrudRepo;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.OAuthAccountEntity;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.OAuthUserInfo;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.repository.OAuthAccountCrudRepo;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberCrudRepo memberRepo;
    private final OAuthAccountCrudRepo oAuthAccountRepo;

    public void join(OAuthUserInfo userInfo) {
        if (oAuthAccountRepo.findByProviderNameAndProviderId(userInfo.getProviderName(), userInfo.getProviderId()).isPresent()) {
            throw new IllegalStateException("This account is already registered.");
        }

        String sub;
        do {
            sub = UUID.randomUUID().toString();
        } while (memberRepo.existsBySub(sub));

        MemberEntity memberEntity = memberRepo.save(new MemberEntity(userInfo, sub));
        oAuthAccountRepo.save(new OAuthAccountEntity(userInfo, memberEntity));
    }
}
