package clickme.clickme.repository;

import clickme.clickme.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByNickname(final String nickname);
}
