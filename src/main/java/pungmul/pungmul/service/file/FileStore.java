package pungmul.pungmul.service.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.file.Image;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public abstract class FileStore {

    public abstract Image saveImageToStorage(Long userId, MultipartFile originImage) throws IOException;

    protected String createFileName(String originalFilename) {
        String uuid = java.util.UUID.randomUUID().toString();
        String ext = extracted(originalFilename);
        return uuid + "." + ext;
    }

    protected String extracted(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    protected abstract String getFullFilePath(String convertedFileName);
}