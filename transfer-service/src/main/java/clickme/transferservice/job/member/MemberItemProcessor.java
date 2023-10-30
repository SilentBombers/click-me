package clickme.transferservice.job.member;

import clickme.transferservice.domain.ProfileUpdateMember;
import clickme.transferservice.service.GithubApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberItemProcessor implements ItemProcessor<NicknameMember, ProfileUpdateMember> {

    private static final String DEFAULT_AVATAR_URL = "https://github.com/%s.png";

    private final GithubApiService githubApiService;

    @Override
    public ProfileUpdateMember process(final NicknameMember member) {
        String avatarUrl = githubApiService.getAvatarUrl(member.getNickname());

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            return new ProfileUpdateMember(member.getNickname(), avatarUrl);
        }
        return new ProfileUpdateMember(member.getNickname(), DEFAULT_AVATAR_URL.formatted(member.getNickname()));
    }
}
