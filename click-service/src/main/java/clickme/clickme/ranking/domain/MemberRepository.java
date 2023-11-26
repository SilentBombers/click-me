package clickme.clickme.ranking.domain;

import clickme.clickme.ranking.domain.exception.NotFoundMemberException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m.profileImageUrl from Member m where m.name = :name")
    Optional<String> findProfileImageUrlByName(@Param("name") String name);

    default String getProfileImageUrlByName(final String name) {
        return findProfileImageUrlByName(name)
                .orElseGet(() -> "https://avatars.githubusercontent.com/u/134919246?v=4");
    }
}
