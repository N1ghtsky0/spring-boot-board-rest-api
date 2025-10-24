package xyz.jiwook.demo.springBootBoardRestApi.domain.oauthAccount;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.Member;
import xyz.jiwook.demo.springBootBoardRestApi.global.common.ImmutableEntity;

@Getter
@Entity(name = "oauth_account")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccount extends ImmutableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerName;

    private String providerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public OAuthAccount(String providerName, String providerId, Member member) {
        this.providerName = providerName;
        this.providerId = providerId;
        this.member = member;
    }
}
