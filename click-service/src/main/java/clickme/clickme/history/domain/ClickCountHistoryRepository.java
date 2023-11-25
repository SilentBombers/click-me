package clickme.clickme.history.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ClickCountHistoryRepository extends JpaRepository<ClickCountHistory, Long> {

    List<ClickCountHistory> findClickCountHistoryByNameAndDateBetween(final String name, final LocalDate startDate, final LocalDate endDate);
}
