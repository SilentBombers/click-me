package clickme.transferservice.job.member.dto;

import java.time.LocalDate;

public record DailyClickCount(String name, LocalDate date, Long clickCount){
}
