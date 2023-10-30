package clickme.transferservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubApiService {
    private static final String GITHUB_API_URL = "https://api.github.com/repos/{nickname}/contributors";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public String getAvatarUrl(final String nickname) {
        String avatarUrl = "";
        String apiUrl = GITHUB_API_URL.replace("{nickname}", nickname);
        URI uri = createUri(apiUrl);
        HttpRequest request = createRequest(uri);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                String responseBody = response.body();
                GithubContributor[] contributors = objectMapper.readValue(responseBody, GithubContributor[].class);
                avatarUrl = getFirstAvatarUrl(contributors);
            }
        } catch (Exception e) {
            log.error("해당 닉네임으로 github에서 데이터를 가져 올 수 없습니다. nickname: {}", nickname);
        }

        return avatarUrl;
    }

    private static URI createUri(final String apiUrl) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
                .build()
                .toUri();
    }

    private static HttpRequest createRequest(final URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
    }

    private String getFirstAvatarUrl(GithubContributor[] contributors) {
        if (contributors != null && contributors.length > 0) {
            return contributors[0].getAvatar_url();
        }
        return "";
    }
}
