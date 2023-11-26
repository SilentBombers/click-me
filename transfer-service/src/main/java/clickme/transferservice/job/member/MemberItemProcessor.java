package clickme.transferservice.job.member;

import clickme.transferservice.job.member.dto.ProfileUpdateMember;
import clickme.transferservice.service.GithubApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberItemProcessor implements ItemProcessor<NameMember, ProfileUpdateMember> {

    private static final String DEFAULT_AVATAR_URL = "https://avatars.githubusercontent.com/u/134919246?v=4";

    private final GithubApiService githubApiService;

    @Override
    public ProfileUpdateMember process(final NameMember member) {
        String avatarUrl = Optional.ofNullable(githubApiService.getAvatarUrl(member.getName()))
                .filter(StringUtils::hasText)
                .orElse(DEFAULT_AVATAR_URL);

        return new ProfileUpdateMember(member.getName(), avatarUrl);
    }
}
