package com.apurva.chat.storage;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Generic file upload used for chat attachments (images/files).
 * POST /api/uploads with form-field "file". Requires a valid token.
 */
@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(originPatterns = "*")
public class UploadController {

    private final FileStorageService storage;

    public UploadController(FileStorageService storage) {
        this.storage = storage;
    }

    @PostMapping
    public UploadResponse upload(@RequestParam("file") MultipartFile file) {
        String url = storage.store(file);
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        String type = contentType.startsWith("image/") ? "IMAGE" : "FILE";
        return new UploadResponse(url, type, file.getOriginalFilename());
    }
}
