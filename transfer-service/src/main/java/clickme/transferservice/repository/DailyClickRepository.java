package clickme.transferservice.repository;

public interface DailyClickRepository {

    Long getClickCount(final String name);

    void deleteKey(final String key);
}
