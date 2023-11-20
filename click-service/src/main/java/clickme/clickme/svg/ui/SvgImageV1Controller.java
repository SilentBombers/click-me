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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/svg-image")
public class SvgImageV1Controller {

    private final SvgImageService svgImageService;

    @GetMapping
    public ResponseEntity<String> getSvgImageByName(@RequestParam final String name) throws IOException, TransformerException {
        final CacheControl cacheControl = CacheControl.noCache();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/svg+xml"))
                .cacheControl(cacheControl)
                .body(svgImageService.generateSvgImage(name));
    }
}
