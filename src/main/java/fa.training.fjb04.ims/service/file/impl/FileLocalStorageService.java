package fa.training.fjb04.ims.service.file.impl;

import fa.training.fjb04.ims.service.file.FileStorageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Getter
@Service
public class FileLocalStorageService implements FileStorageService {

    @Value("C:\\Users\\Admin\\Desktop\\CV")
    String fileLocation;

    @Override
    public String saveFile(MultipartFile file,String folder) throws IOException {
        String uuid = UUID.randomUUID().toString();
        Path relativePath = getRelativePath(folder, uuid+"_"+file.getOriginalFilename());
        createFolder(folder);
        file.transferTo(Paths.get(fileLocation).resolve(relativePath));
        return relativePath.toString();
    }

    private void createFolder(String folder) throws IOException {
        if (!StringUtils.hasText(folder)) {
            return;
        }
        Path folderPath = Paths.get(fileLocation).resolve(folder);
        if (Files.notExists(folderPath)) {
            Files.createDirectories(folderPath);
        }
    }

    @Override
    public Path getRelativePath(String folder, String fileName){
        Objects.requireNonNull(fileName);

        return StringUtils.hasText(folder) ?
                Paths.get(folder).resolve(fileName) :
                Paths.get(fileName);
    }

    @Override
    public Resource loadFileAsResource(String relativePath) throws MalformedURLException, FileNotFoundException {
        Path absolute = Paths.get(fileLocation + relativePath);
        Resource resource = new UrlResource(absolute.toUri());
        if (resource.exists()) {
            return resource;
        }
        throw new FileNotFoundException("Can not find the file with url: " + relativePath);
    }

    @Override
    public String getFileLocation() {

        return fileLocation;
    }

}

