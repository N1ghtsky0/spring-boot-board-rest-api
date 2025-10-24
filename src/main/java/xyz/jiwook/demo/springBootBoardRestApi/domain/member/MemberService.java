package xyz.jiwook.demo.springBootBoardRestApi.domain.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(String email) {
        String sub = generateUniqueSub();
        return memberRepository.save(new Member(email, sub));
    }

    public Member getMemberBySub(String sub) {
        return memberRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + sub));
    }

    private String generateUniqueSub() {
        String sub;
        do {
            sub = UUID.randomUUID().toString();
        } while (memberRepository.existsBySub(sub));
        return sub;
    }
}
