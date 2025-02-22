package clickme.clickme.ranking.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AbstractRootEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long clickCount;

    @Column
    private String name;

    @Column
    private String profileImageUrl;

    @Column
    private String svgUrl;

    public Member(final Long clickCount, final String name, final String profileImageUrl) {
        this.clickCount = clickCount;
        this.name = name.toLowerCase();
        this.profileImageUrl = profileImageUrl;
    }

    public void updateSvgUrl(final String svgUrl) {
        this.svgUrl = svgUrl;
    }
}
