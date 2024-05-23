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
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ClickCountHistoryService {

    private final ClickCountHistoryRepository clickCountHistoryRepository;

    public ClickCountHistoriesResponse findClickCountHistoryByNameAndDateBetween(final String name) {
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusDays(6);

        final List<ClickCountHistory> histories = clickCountHistoryRepository
                .findClickCountHistoryByNameAndDateBetweenOrderByDate(name, startDate, endDate);
        final Map<LocalDate, Long> dateToClickCount = mapHistoriesToClickCount(histories);
        List<ClickCountHistoryResponse> responses = buildResponse(startDate, dateToClickCount);

        return new ClickCountHistoriesResponse(responses);
    }

    private static Map<LocalDate, Long> mapHistoriesToClickCount(final List<ClickCountHistory> histories) {
        return histories.stream()
                .collect(Collectors.toMap(
                        ClickCountHistory::getDate,
                        ClickCountHistory::getClickCount)
                );
    }

    private static List<ClickCountHistoryResponse> buildResponse(final LocalDate startDate, final Map<LocalDate, Long> dateToClickCount) {
        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate date = startDate.plusDays(i);
                    return new ClickCountHistoryResponse(date, dateToClickCount.getOrDefault(date, 0L));
                })
                .toList();
    }
}
