package clickme.clickme.history.application.dto;

import java.time.LocalDate;

public record ClickCountHistoryResponse(LocalDate date, Long ClickCount) {
}
