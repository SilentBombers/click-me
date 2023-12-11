package clickme.clickme.history.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "click_count_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClickCountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private LocalDate date;

    @Column
    private Long clickCount;

    public ClickCountHistory(final String name, final LocalDate date, final Long clickCount) {
        this.name = name;
        this.date = date;
        this.clickCount = clickCount;
    }

    @Override
    public String toString() {
        return "DailyClickCount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", clickCount=" + clickCount +
                '}';
    }
}
