package clickme.transferservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RedisKeyGenerator {

    private static final String DAILY_CLICK_COUNT_KEY = "%s:dailyClickCount";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String CLICK_COUNT_KEY = "clicks";

    public static String getDailyClickCountKey() {
        return DAILY_CLICK_COUNT_KEY.formatted(LocalDateTime.now().format(formatter));
    }

    public static String getCLickCountKey() {
        return CLICK_COUNT_KEY;
    }
}
