package xyz.jiwook.demo.springBootBoardRestApi.domain.member.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import xyz.jiwook.demo.springBootBoardRestApi.domain.oauth2.model.OAuthUserInfo;
import xyz.jiwook.demo.springBootBoardRestApi.global.common.MutableEntity;

@Getter
@Entity(name = "member")
public class MemberEntity extends MutableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;

    private String email;

    public MemberEntity(OAuthUserInfo oAuthUserInfo) {
        this.email = oAuthUserInfo.getEmail();
    }

    protected MemberEntity() {

    }
}
