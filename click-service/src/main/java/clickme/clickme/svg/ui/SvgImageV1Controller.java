package clickme.clickme.svg.ui;

import clickme.clickme.svg.application.SvgImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/svg-image")
public class SvgImageV1Controller {

    private static final CacheControl DEFAULT_CACHE_CONTROL = CacheControl.maxAge(1, TimeUnit.SECONDS);
    private static final MediaType SVG = MediaType.valueOf("image/svg+xml");

    private final SvgImageService svgImageService;

    @GetMapping
    public ResponseEntity<String> getNonClickableSvgImageByName(@RequestParam final String name) throws IOException, TransformerException {
        return ResponseEntity.ok()
                .contentType(SVG)
                .cacheControl(DEFAULT_CACHE_CONTROL)
                .body(svgImageService.generateNonClickableSvgImage(name));
    }

    @GetMapping("/increment")
    public ResponseEntity<String> getClickableSvgImageByName(@RequestParam final String name) throws IOException, TransformerException {
        return ResponseEntity.ok()
                .contentType(SVG)
                .cacheControl(DEFAULT_CACHE_CONTROL)
                .body(svgImageService.generateClickableSvgImage(name));
    }
}
