package clickme.transferservice.service;

import clickme.transferservice.service.exception.GithubApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubApiService {
    private static final String GITHUB_API_URL = "https://api.github.com/users/{nickname}";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public String getAvatarUrl(final String nickname) {
        String avatarUrl = "";
        String apiUrl = GITHUB_API_URL.replace("{nickname}", nickname);
        URI uri = createUri(apiUrl);
        HttpRequest request = createRequest(uri);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            avatarUrl = parseGithubUser(response, avatarUrl);
        } catch (IOException | InterruptedException e) {
            throw new GithubApiException();
        }
        return avatarUrl;
    }

    private String parseGithubUser(final HttpResponse<String> response, String avatarUrl) throws JsonProcessingException {
        if (response.statusCode() == HttpStatus.OK.value()) {
            String responseBody = response.body();
            GithubUser user = objectMapper.readValue(responseBody, GithubUser.class);
            avatarUrl = user.getAvatarUrl();
        }
        return avatarUrl;
    }

    private URI createUri(final String apiUrl) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
                .build()
                .toUri();
    }

    private HttpRequest createRequest(final URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
    }
}
