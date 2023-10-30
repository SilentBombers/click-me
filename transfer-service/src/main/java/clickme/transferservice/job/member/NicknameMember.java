package clickme.transferservice.job.member;

public class NicknameMember {

    private Long id;
    private String nickname;

    public NicknameMember() {
    }

    public NicknameMember(final Long id, final String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "NicknameMember{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
