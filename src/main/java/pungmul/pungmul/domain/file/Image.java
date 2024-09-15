package pungmul.pungmul.domain.file;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    private Long id;
    private String originalFilename;
    private String convertedFileName;
    private String fullFilePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime createdAt;
}
