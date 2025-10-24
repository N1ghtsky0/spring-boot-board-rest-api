package xyz.jiwook.demo.springBootBoardRestApi.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.jiwook.demo.springBootBoardRestApi.global.common.MutableEntity;

@Getter
@Entity(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends MutableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String sub;

    public Member(String email, String sub) {
        this.email = email;
        this.sub = sub;
    }
}
