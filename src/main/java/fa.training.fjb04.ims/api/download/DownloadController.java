package fa.training.fjb04.ims.api.download;

import fa.training.fjb04.ims.service.file.FileStorageService;
import fa.training.fjb04.ims.service.file.impl.FileLocalStorageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/download")
public class DownloadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);
    private final FileStorageService fileLocalCandidateService;

    @GetMapping("/{filename}")
    public HttpEntity<ByteArrayResource> createExcelWithTaskConfiguration(@PathVariable("filename") String fileName) throws IOException {

        String filePath = "/static/files/" + fileName;
        InputStream inputStream = getClass().getResourceAsStream(filePath);


        if (inputStream == null) {
            LOGGER.error("File not found: {}", filePath);

            return null;
        }

        byte[] excelContent;
        try {

            excelContent = IOUtils.toByteArray(inputStream);
        } finally {

            inputStream.close();
        }

        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return new HttpEntity<>(new ByteArrayResource(excelContent), header);
    }
    @GetMapping("/cv/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) throws IOException {

        File file = new File(fileLocalCandidateService.getFileLocation() + "/" + fileLocalCandidateService.getRelativePath("candidate",fileName));


        byte[] data = FileUtils.readFileToByteArray(file);

        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}