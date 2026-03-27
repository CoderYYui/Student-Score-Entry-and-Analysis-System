package com.student.score.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot);
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("上传文件不能为空");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(file.getOriginalFilename(), "attachment"));
        String extension = "";
        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex >= 0) {
            extension = originalFilename.substring(extensionIndex);
        }

        String storedFilename = UUID.randomUUID() + extension;
        Path targetFile = uploadRoot.resolve(storedFilename).normalize();
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return storedFilename;
    }

    public Resource loadAsResource(String filename) throws MalformedURLException {
        Path filePath = uploadRoot.resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            throw new MalformedURLException("文件不存在: " + filename);
        }
        return resource;
    }

    public String probeContentType(String filename) {
        try {
            Path filePath = uploadRoot.resolve(filename).normalize();
            String contentType = Files.probeContentType(filePath);
            return contentType == null ? "application/octet-stream" : contentType;
        } catch (IOException ignored) {
            return "application/octet-stream";
        }
    }
}