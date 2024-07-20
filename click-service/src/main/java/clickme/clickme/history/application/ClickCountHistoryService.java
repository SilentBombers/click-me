package clickme.clickme.history.application;

import clickme.clickme.history.application.dto.ClickCountHistoriesResponse;
import clickme.clickme.history.application.dto.ClickCountHistoryResponse;
import clickme.clickme.history.domain.ClickCountHistory;
import clickme.clickme.history.domain.ClickCountHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickCountHistoryService {

    private final ClickCountHistoryRepository clickCountHistoryRepository;

    public ClickCountHistoriesResponse findClickCountHistoryByNameAndDateBetween(final String name) {
        final LocalDate endDate = LocalDate.now().minusDays(1);
        final LocalDate startDate = endDate.minusDays(6);

        final List<ClickCountHistory> histories = clickCountHistoryRepository
                .findClickCountHistoryByNameAndDateBetweenOrderByDate(name, startDate, endDate);

        final Map<LocalDate, Long> dateToClickCount = mapHistoriesToClickCount(histories);
        final List<ClickCountHistoryResponse> responses = buildResponse(startDate, endDate, dateToClickCount);

        return new ClickCountHistoriesResponse(responses);
    }

    private Map<LocalDate, Long> mapHistoriesToClickCount(final List<ClickCountHistory> histories) {
        return histories.stream()
                .collect(Collectors.toMap(
                        ClickCountHistory::getDate,
                        ClickCountHistory::getClickCount
                ));
    }

    private List<ClickCountHistoryResponse> buildResponse(
            final LocalDate startDate,
            final LocalDate endDate,
            final Map<LocalDate, Long> dateToClickCount
    ) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(date -> new ClickCountHistoryResponse(date, dateToClickCount.getOrDefault(date, 0L)))
                .toList();
    }
}
