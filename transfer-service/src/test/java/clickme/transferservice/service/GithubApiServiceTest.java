package clickme.transferservice.service;

import clickme.transferservice.service.exception.GithubApiException;
import clickme.transferservice.support.HttpClientSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubApiServiceTest {

    @Mock
    private HttpClientSupport httpClientSupport;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GithubApiService githubApiService;

    @Test
    void 정상적인_nickname일_경우_이미지를_정상적으로_가져온다() throws IOException, InterruptedException {
        String expectedAvatarUrl = "https://example.com/avatar.jpg";
        String nickname = "testUser";
        String mockResponseBody = "{\"avatar_url\":\"" + expectedAvatarUrl + "\"}";
        when(httpClientSupport.sendRequest(any())).thenReturn(mockResponseBody);
        when(objectMapper.readValue(mockResponseBody, GithubUser.class)).thenReturn(new GithubUser(expectedAvatarUrl));

        String actualAvatarUrl = githubApiService.getAvatarUrl(nickname);

        assertThat(actualAvatarUrl).isEqualTo(expectedAvatarUrl);
    }

    @Test
    void github_api_호출중에_문제가_발생할_경우_예외가_발생한다() throws IOException, InterruptedException {
        String nickname = "user";
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(java.net.URI.create("https://api.github.com/users/" + nickname)).GET().build();
        when(httpClientSupport.sendRequest(any())).thenThrow(IOException.class);

        assertThatThrownBy(() -> githubApiService.getAvatarUrl(nickname))
                .isInstanceOf(GithubApiException.class);
    }
}
