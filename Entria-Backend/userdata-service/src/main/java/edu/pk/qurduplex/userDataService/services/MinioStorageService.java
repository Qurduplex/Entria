package edu.pk.qurduplex.userDataService.services;

import edu.pk.qurduplex.appRegistryService.config.MinioProperties;
import edu.pk.qurduplex.appRegistryService.exceptions.FileStorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String fileName = folder + "/" + UUID.randomUUID() + extension;

            InputStream inputStream = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getPublicBucket())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String fileUrl = String.format("%s/%s/%s",
                    minioProperties.getExternalUrl(),
                    minioProperties.getPublicBucket(),
                    fileName);

            log.info("File uploaded successfully to MinIO: {}", fileUrl);
            return fileUrl;

        } catch (Exception e) {
            log.error("Error occurred while uploading file to MinIO", e);
            throw new FileStorageException("Failed to store file: " + e.getMessage());
        }
    }
}