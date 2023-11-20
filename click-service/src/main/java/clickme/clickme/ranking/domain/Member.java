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
@Table(name = "members")
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

    public Member(final Long clickCount, final String name, final String profileImageUrl) {
        this.clickCount = clickCount;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
