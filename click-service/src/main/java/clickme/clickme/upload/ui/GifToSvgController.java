package clickme.clickme.upload.ui;

import clickme.clickme.upload.application.GifToSvgService;
import clickme.clickme.upload.application.dto.SvgConvertResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class GifToSvgController {

    private final GifToSvgService gifToSvgService;

    public GifToSvgController(GifToSvgService gifToSvgService) {
        this.gifToSvgService = gifToSvgService;
    }

    @PostMapping("/convert")
    public ResponseEntity<SvgConvertResponse> convertGifToSvg(@RequestParam("gif") MultipartFile file) {
        return ResponseEntity.ok(gifToSvgService.convertGifToSvg(file));
    }
}
