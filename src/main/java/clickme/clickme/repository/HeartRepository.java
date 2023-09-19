package clickme.clickme.repository;

public interface HeartRepository {

    void increaseCount(String id);

    void add(String id);

    Long findById(String id);
}
