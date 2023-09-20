package clickme.clickme.controller;

import clickme.clickme.service.EmojiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;

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
