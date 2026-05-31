package edu.pk.qurduplex.appRegistryService.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Uploads a file to the storage service and returns the URL of the uploaded file.
     *
     * @param file   The file to be uploaded.
     * @param folder The folder in which to store the file.
     * @return The URL of the uploaded file.
     */
    String uploadFile(MultipartFile file, String folder);
}
