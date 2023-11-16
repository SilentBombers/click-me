package clickme.transferservice.job.member;

import clickme.transferservice.domain.ProfileUpdateMember;
import clickme.transferservice.service.GithubApiService;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberItemProcessor implements ItemProcessor<NicknameMember, ProfileUpdateMember> {

    private static final String DEFAULT_AVATAR_URL = "https://avatars.githubusercontent.com/u/134919246?v=4";

    private final GithubApiService githubApiService;

    @Override
    public ProfileUpdateMember process(final NicknameMember member) {
        String avatarUrl = Optional.ofNullable(githubApiService.getAvatarUrl(member.getNickname()))
                .filter(StringUtils::hasText)
                .orElse(DEFAULT_AVATAR_URL);

        return new ProfileUpdateMember(member.getNickname(), avatarUrl);
    }
}
