package clickme.clickme.controller;

import clickme.clickme.service.EmojiService;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HeartController {

    private final EmojiService emojiService;

    @GetMapping("/show")
    public String show(@RequestParam String URI) {
        return "heart.html";
    }

    @GetMapping("/fonts")
    public String fonts() {
        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = e.getAllFonts();
        for (Font font : fonts) {
            System.out.println(font.getFontName());
        }
        return Arrays.toString(fonts);
    }

    @GetMapping(value = "/stringToImage", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] stringToImage() {
        return emojiService.serveImage("test");
    }
}
