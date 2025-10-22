package xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model;

import jakarta.persistence.*;
import lombok.Getter;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.model.MemberEntity;
import xyz.jiwook.demo.springBootBoardRestApi.global.common.ImmutableEntity;

@Getter
@Entity(name = "oauth_account")
public class OAuthAccountEntity extends ImmutableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    private String providerName;
    private String providerId;

    @ManyToOne
    @JoinColumn(name = "member_seq")
    private MemberEntity member;

    protected OAuthAccountEntity() {
    }

    public OAuthAccountEntity(OAuthUserInfo oAuthUserInfo, MemberEntity member) {
        this.providerName = oAuthUserInfo.getProviderName();
        this.providerId = oAuthUserInfo.getProviderId();
        this.member = member;
    }

}
