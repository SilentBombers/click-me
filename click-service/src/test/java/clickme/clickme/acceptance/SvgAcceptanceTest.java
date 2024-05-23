package clickme.clickme.acceptance;

import clickme.clickme.svg.application.SvgImageService;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class SvgAcceptanceTest extends AcceptanceTest {

    @MockBean
    private SvgImageService svgImageService;

    @Test
    void 비활성_svg_이미지를_가져온다() throws Exception {
        String name = "seungpang";
        String svgContent = "<svg>Non-clickable content</svg>";

        when(svgImageService.generateNonClickableSvgImage(isA(String.class))).thenReturn(svgContent);
        ExtractableResponse<Response> response = 비활성_svg_이미지_조회( name);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.contentType()).isEqualTo("image/svg+xml"),
                () -> assertThat(response.body().asString()).isEqualTo(svgContent)
        );
    }



    @Test
    void 활성_svg_이미지를_가져온다() throws Exception {
        String name = "seungpang";
        String svgContent = "<svg>Clickable content</svg>";

        when(svgImageService.generateClickableSvgImage(isA(String.class))).thenReturn(svgContent);
        ExtractableResponse<Response> response = 활성_svg_이미지_조회(name);

        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.contentType()).isEqualTo("image/svg+xml"),
                () -> assertThat(response.body().asString()).isEqualTo(svgContent)
        );
    }

    @Test
    void 이름_파라미터_누락된_경우_비활성_SVG_이미지_가져오지_못한다() {
        ExtractableResponse<Response> response = AcceptanceFixture.get("/api/v1/svg-image");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 빈_이름_파라미터로_비활성_SVG_이미지를_가져온다() {
        ExtractableResponse<Response> response = 비활성_svg_이미지_조회("");

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 서비스_예외_발생시_비활성_SVG_이미지를_가져오지_못하고_예외가_발생한다() throws Exception {
        String name = "seungpang";

        when(svgImageService.generateNonClickableSvgImage(isA(String.class))).thenThrow(new IOException("Service exception"));

        ExtractableResponse<Response> response = 비활성_svg_이미지_조회(name);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 서비스_예외_발생시_활성_SVG_이미지를_가져오지_못하고_예외가_발생한다() throws Exception {
        String name = "seungpang";

        when(svgImageService.generateClickableSvgImage(isA(String.class))).thenThrow(new IOException("Service exception"));

        ExtractableResponse<Response> response = 활성_svg_이미지_조회(name);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    void 비활성_SVG_이미지의_캐시_컨트롤_헤더에_값이_정상적인지_확인한다() throws Exception {
        String name = "seungpang";
        String svgContent = "<svg>Non-clickable content</svg>";

        when(svgImageService.generateNonClickableSvgImage(isA(String.class))).thenReturn(svgContent);

        ExtractableResponse<Response> response = 비활성_svg_이미지_조회(name);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.header("Cache-Control")).isEqualTo("max-age=1");
    }

    @Test
    void 활성_SVG_이미지의_캐시_컨트롤_헤더에_값이_정상적인지_확인한다() throws Exception {
        String name = "seungpang";
        String svgContent = "<svg>Clickable content</svg>";

        when(svgImageService.generateClickableSvgImage(isA(String.class))).thenReturn(svgContent);

        ExtractableResponse<Response> response = 활성_svg_이미지_조회(name);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.header("Cache-Control")).isEqualTo("max-age=1");
    }

    private static ExtractableResponse<Response> 비활성_svg_이미지_조회(final String name) {
        return AcceptanceFixture.get("/api/v1/svg-image?name=" + name);
    }

    private static ExtractableResponse<Response> 활성_svg_이미지_조회(final String name) {
        return AcceptanceFixture.get("/api/v1/svg-image/increment?name=" + name);
    }
}
