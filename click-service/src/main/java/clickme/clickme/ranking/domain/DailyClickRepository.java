package clickme.clickme.ranking.domain;

public interface DailyClickRepository {

    void increaseCount(final String name);

    void add(final String name);
}
