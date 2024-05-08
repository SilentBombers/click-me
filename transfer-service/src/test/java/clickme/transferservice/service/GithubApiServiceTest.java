package clickme.transferservice.service;

import clickme.transferservice.service.exception.GithubApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubApiServiceTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private GithubApiService githubApiService;

    @Test
    void 정상적인_nickname일_경우_이미지를_정상적으로_가져온다() throws IOException, InterruptedException {
        String expectedAvatarUrl = "http://example.com/avatar.jpg";
        String nickname = "testUser";
        String responseBody = "{\"avatar_url\":\"" + expectedAvatarUrl + "\"}";
        HttpResponse<String> mockResponse = mockHttpResponse(HttpStatus.OK.value(), responseBody);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenReturn(mockResponse);
        when(objectMapper.readValue(responseBody, GithubUser.class)).thenReturn(new GithubUser(expectedAvatarUrl));

        String actualAvatarUrl = githubApiService.getAvatarUrl(nickname);

        assertThat(actualAvatarUrl).isEqualTo(expectedAvatarUrl);
    }

    @Test
    void github_api_호출중에_문제가_발생할_경우_예외가_발생한다() throws IOException, InterruptedException {
        String nickname = "user";
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(java.net.URI.create("https://api.github.com/users/" + nickname)).GET().build();
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass()))).thenThrow(IOException.class);

        assertThatThrownBy(() -> githubApiService.getAvatarUrl(nickname))
                .isInstanceOf(GithubApiException.class);

        verify(httpClient).send(eq(httpRequest), any(HttpResponse.BodyHandlers.ofString().getClass()));
    }

    private HttpResponse<String> mockHttpResponse(int statusCode, String body) {
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.body()).thenReturn(body);
        return response;
    }
}
