package clickme.clickme.history.application;

import clickme.clickme.history.application.dto.ClickCountHistoryResponse;
import clickme.clickme.history.domain.ClickCountHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClickCountHistoryService {

    private final ClickCountHistoryRepository clickCountHistoryRepository;

    public List<ClickCountHistoryResponse> findClickCountHistoryByNameAndDateBetween(final String name) {
        final LocalDate endDate = LocalDate.now();
        final LocalDate startDate = endDate.minusDays(6);
        final List<ClickCountHistoryResponse> histories = getHistories(name, startDate, endDate);

        Map<LocalDate, ClickCountHistoryResponse> dateToHistory = histories.stream()
                .collect(Collectors.toMap(
                        history -> history.date(),
                        Function.identity())
                );

        return fillMissingDates(startDate, endDate, dateToHistory);
    }

    private List<ClickCountHistoryResponse> getHistories(
            final String name,
            final LocalDate startDate,
            final LocalDate endDate
    ) {
        return clickCountHistoryRepository.findClickCountHistoryByNameAndDateBetweenOrderByDate(name, startDate, endDate)
                .stream()
                .map(clickCountHistory -> new ClickCountHistoryResponse(clickCountHistory.getDate(), clickCountHistory.getClickCount()))
                .toList();
    }

    private List<ClickCountHistoryResponse> fillMissingDates(
            final LocalDate startDate,
            final LocalDate endDate,
            final Map<LocalDate, ClickCountHistoryResponse> dateToHistory
    ) {
        List<ClickCountHistoryResponse> result = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            result.add(dateToHistory.getOrDefault(date, new ClickCountHistoryResponse(date, 0L)));
        }
        return result;
    }
}
