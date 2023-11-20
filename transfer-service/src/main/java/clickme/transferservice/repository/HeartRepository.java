package clickme.transferservice.repository;

public interface HeartRepository {

    Long getClickCount(final String nickname);

    void deleteKey(final String key);
}
