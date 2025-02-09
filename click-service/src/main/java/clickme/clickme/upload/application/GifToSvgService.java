package clickme.clickme.upload.application;

import clickme.clickme.upload.application.dto.SvgConvertResponse;
import clickme.clickme.upload.application.exception.SvgConvertException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GifToSvgService {

    public static final String GIF = ".gif";

    private final GcsService gcsService;

    public SvgConvertResponse convertGifToSvg(MultipartFile gifFile) {
        final UUID uuid = UUID.randomUUID();
        Path uploadDir = null;
        Path framesDir = null;
        try {
            uploadDir = Files.createTempDirectory("uploads_");
            framesDir = Files.createTempDirectory("frames_").resolve(uuid.toString());
            Files.createDirectories(framesDir);

            Path gifPath = uploadDir.resolve(uuid + GIF);
            try (InputStream inputStream = gifFile.getInputStream()) {
                Files.copy(inputStream, gifPath, StandardCopyOption.REPLACE_EXISTING);
            }

            String outputPattern = framesDir.resolve("frame-%03d.png").toString();
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg", "-i", gifPath.toString(), outputPattern
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("ffmpeg 프로세스가 비정상 종료되었습니다. exitCode: " + exitCode);
            }

            File[] frames = framesDir.toFile().listFiles((dir, name) -> name.endsWith(".png"));
            if (frames == null || frames.length == 0) {
                throw new IOException("GIF 프레임을 추출할 수 없습니다.");
            }

            String svgContent = generateSvg(frames);

            String svgUrl = gcsService.uploadSvg(svgContent);
            log.info("SVG 파일이 저장되었습니다: {}", svgUrl);
            return new SvgConvertResponse(svgUrl);
        } catch (IOException | InterruptedException e) {
            log.error("GIF 변환 중 오류 발생: {}", e.getMessage());
            throw new SvgConvertException();
        } finally {
            if (framesDir != null) {
                try {
                    FileUtils.deleteDirectory(framesDir.toFile());
                } catch (IOException e) {
                    log.warn("framesDir 삭제 실패: {}", e.getMessage());
                }
            }
            if (uploadDir != null) {
                try {
                    FileUtils.deleteDirectory(uploadDir.toFile());
                } catch (IOException e) {
                    log.warn("uploadDir 삭제 실패: {}", e.getMessage());
                }
            }
        }
    }

    private String generateSvg(File[] frames) throws IOException {
        double rate = 0.1;
        StringBuilder svg = new StringBuilder();
        svg.append("<svg width=\"300\" height=\"150\" viewBox=\"0 0 300 150\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n<style>\n");
        svg.append("@keyframes anim {");
        svg.append("0% { opacity: 1; }\n");
        svg.append("4.16% { opacity: 1; }\n");
        svg.append("8.33% { opacity: 0; }\n");
        svg.append("100% { opacity: 0; } }\n");

        svg.append("image { opacity: 0; animation: anim ")
                .append(rate * frames.length).append("s linear infinite; }\n");

        StringBuilder svgImages = new StringBuilder();
        for (int i = 0; i < frames.length; i++) {
            String base64 = encodeImageToBase64(frames[i]);
            svg.append("image#img").append(i)
                    .append(" { animation-delay: ").append(String.format("%.2f", i * rate)).append("s; }\n");

            svgImages.append("<image id=\"img").append(i)
                    .append("\" xlink:href=\"data:image/png;base64,").append(base64)
                    .append("\" width=\"100%\" height=\"100%\" />\n");
        }

        svg.append("</style>\n");
        svg.append(svgImages);
        svg.append("<circle id=\"my-circle\" r=\"28\" fill=\"rgba(255, 255, 255, 0.8)\" stroke=\"black\" stroke-width=\"2\"/>");
        svg.append("<text id=\"my-text\" x=\"250\" y=\"38\" font-size=\"20\" font-weight=\"bold\" text-anchor=\"middle\" fill=\"black\"></text>");
        svg.append("</svg>");

        return svg.toString();
    }

    private String encodeImageToBase64(File imageFile) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(imageFile);
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
