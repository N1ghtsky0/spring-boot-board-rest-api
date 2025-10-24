package xyz.jiwook.demo.springBootBoardRestApi.domain.member.repo;

import org.springframework.data.repository.CrudRepository;
import xyz.jiwook.demo.springBootBoardRestApi.domain.member.model.MemberEntity;

public interface MemberCrudRepo extends CrudRepository<MemberEntity, Long> {
    boolean existsBySub(String sub);
}
