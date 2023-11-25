package clickme.clickme.history.application;

import clickme.clickme.history.application.dto.ClickCountHistoryResponse;
import clickme.clickme.history.domain.ClickCountHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClickCountHistoryService {

    private final ClickCountHistoryRepository clickCountHistoryRepository;

    public List<ClickCountHistoryResponse> findClickCountHistoryByNameAndDateBetween(final String name) {
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusDays(6);
        return clickCountHistoryRepository.findClickCountHistoryByNameAndDateBetween(name, startDate, endDate)
                .stream()
                .map(clickCountHistory -> new ClickCountHistoryResponse(clickCountHistory.getDate(), clickCountHistory.getClickCount()))
                .toList();
    }
}
