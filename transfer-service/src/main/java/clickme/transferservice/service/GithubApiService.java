package clickme.transferservice.service;

import clickme.transferservice.service.exception.GithubApiException;
import clickme.transferservice.support.HttpClientSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubApiService {
    private static final String GITHUB_API_URL = "https://api.github.com/users/{nickname}";

    private final HttpClientSupport httpClientSupport;
    private final ObjectMapper objectMapper;

    public String getAvatarUrl(final String nickname) {
        String avatarUrl = "";
        String apiUrl = GITHUB_API_URL.replace("{nickname}", nickname);
        URI uri = createUri(apiUrl);

        try {
            String responseBody = httpClientSupport.sendRequest(uri);
            GithubUser user = objectMapper.readValue(responseBody, GithubUser.class);
            avatarUrl = user.getAvatarUrl();
        } catch (IOException | InterruptedException e) {
            throw new GithubApiException();
        }
        return avatarUrl;
    }

    private URI createUri(final String apiUrl) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
                .build()
                .toUri();
    }
}
