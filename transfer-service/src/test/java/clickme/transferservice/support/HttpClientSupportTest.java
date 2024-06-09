package clickme.transferservice.support;

import clickme.transferservice.support.exception.HttpRequestFailedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpClientSupportTest {

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private HttpClientSupport httpClientSupport;

    @Test
    void 요청이_정상적으로_성공할_경우() throws IOException, InterruptedException {
        URI uri = URI.create("https://example.com");
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        String expected = "response body";
        when(mockResponse.body()).thenReturn(expected);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        String response = httpClientSupport.sendRequest(uri);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    void body를_포함하고_있는_요청이_성공할_경우() throws IOException, InterruptedException {
        String url = "https://example.com";
        String jsonBody = "{\"key\":\"value\"}";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.OK.value());
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        assertDoesNotThrow(() -> httpClientSupport.sendPostRequestWithBody(url, jsonBody));

        ArgumentCaptor<HttpRequest> captor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(captor.capture(), any(HttpResponse.BodyHandler.class));
        assertThat(url).isEqualTo(captor.getValue().uri().toString());
    }

    @Test
    void body를_포함하고_있는_요청이_실패할_경우_예외발생() throws IOException, InterruptedException {
        String url = "https://example.com";
        String jsonBody = "{\"key\":\"value\"}";
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST.value());
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);

        assertThatThrownBy(() -> httpClientSupport.sendPostRequestWithBody(url, jsonBody))
                .isInstanceOf(HttpRequestFailedException.class);
    }
}
