package xyz.jiwook.demo.springBootBoardRestApi.domain.member;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {
    boolean existsBySub(String sub);

    Optional<Member> findBySub(String sub);
}
