package fa.training.fjb04.ims.util.validation.candidate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ValidFormatFileValidator implements ConstraintValidator<ValidFormatFile, MultipartFile> {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("pdf", "doc", "docx");

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (Objects.isNull(value) || value.isEmpty()) {
            return true;
        }

        String originalFilename = value.getOriginalFilename();

        if (Objects.isNull(originalFilename)) {
            return false;
        }

        String fileExtension = getFileExtension(originalFilename);

        if (Objects.isNull(fileExtension)) {
            return false;
        }

        return ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase());
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        }
        return fileName.substring(dotIndex + 1);
    }
}
