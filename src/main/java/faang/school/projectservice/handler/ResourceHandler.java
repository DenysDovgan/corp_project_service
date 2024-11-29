package faang.school.projectservice.handler;

import faang.school.projectservice.exception.UnsupportedResourceException;
import faang.school.projectservice.util.CustomMultipartFile;
import faang.school.projectservice.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceHandler {
    private final ResourceValidator resourceValidator;

    public BufferedImage getImageFromMultipartFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new UnsupportedResourceException(
                        String.format("Uploaded file '%s' is not a valid image.", file.getOriginalFilename()));
            }

            log.info("File '{}' is a valid image.", file.getOriginalFilename());

            return image;
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading file '%s'.", file.getOriginalFilename()), e);
        }
    }

    public BufferedImage resizeImage(BufferedImage image, int maxWidth, int maxHeight) {
        boolean isSquare = resourceValidator.isSquareImage(image);

        image = Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, maxWidth);
        if (!isSquare && image.getHeight() > maxHeight) {
            image = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, maxHeight);
        }

        return image;
    }

    public MultipartFile convertImageToMultipartFile(MultipartFile file, BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] resizedImageBytes;

        try {
            String format = getResourceExtension(file);
            ImageIO.write(image, format, baos);
            resizedImageBytes = baos.toByteArray();
            baos.close();
        } catch (IOException e) {
            throw new IllegalStateException(
                    String.format("An error occurred while converting image into MultipartFile '%s'",
                            file.getOriginalFilename()), e);
        }

        return new CustomMultipartFile(
                file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                resizedImageBytes
        );
    }

    private String getResourceExtension(MultipartFile file) {
        MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();

        try {
            MimeType mimeType = mimeTypes.forName(file.getContentType());
            return mimeType.getExtension().substring(1);
        } catch (MimeTypeException e) {
            throw new IllegalStateException(
                    String.format("An error occurred while getting file '%s' extension.",
                            file.getOriginalFilename()), e);
        }
    }
}