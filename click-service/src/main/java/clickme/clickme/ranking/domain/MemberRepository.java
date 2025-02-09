package clickme.clickme.ranking.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);

    @Query("select m.svgUrl from Member m where m.name = :name")
    Optional<String> findSvgImageName(@Param("name") String name);

    @Query("select m.profileImageUrl from Member m where m.name = :name")
    Optional<String> findProfileImageUrlByName(@Param("name") String name);

    default String getProfileImageUrlByName(final String name) {
        return findProfileImageUrlByName(name)
                .orElse("https://avatars.githubusercontent.com/u/134919246?v=4");
    }

    default Member getMemberByName(String name) {
        return findByName(name)
                .orElse(new Member(0L, name, null));
    }
}
