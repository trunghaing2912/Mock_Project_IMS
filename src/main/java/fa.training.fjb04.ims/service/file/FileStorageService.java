package fa.training.fjb04.ims.service.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public interface FileStorageService {
    String saveFile(MultipartFile file, String folder) throws IOException;

    Resource loadFileAsResource(String relativePath) throws MalformedURLException, FileNotFoundException;

    String getFileLocation();

    Path getRelativePath(String folder, String fileName);

}
