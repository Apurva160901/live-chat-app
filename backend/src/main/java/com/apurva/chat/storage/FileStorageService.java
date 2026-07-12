package com.apurva.chat.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Saves uploaded files (avatars, chat attachments) to a folder on disk and
 * returns the public URL to reach them (served at /uploads/**).
 */
@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String dir) throws IOException {
        this.root = Paths.get(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    /** Store a file under a random name; return its public URL like "/uploads/ab12.png". */
    public String store(MultipartFile file) {
        try {
            String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }
            String name = UUID.randomUUID().toString().replace("-", "") + ext;
            file.transferTo(root.resolve(name));
            return "/uploads/" + name;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Path getRoot() {
        return root;
    }
}
