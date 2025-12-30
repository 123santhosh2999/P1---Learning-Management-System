package com.example.lms.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

  private final Path uploadsDir;

  public UploadService(@Value("${app.uploads-dir}") String uploadsDir) {
    this.uploadsDir = Path.of(uploadsDir);
  }

  public String save(MultipartFile file) {
    if (file == null || file.isEmpty()) return null;

    try {
      Files.createDirectories(uploadsDir);
      String original = file.getOriginalFilename();
      String ext = "";
      if (original != null && original.contains(".")) {
        ext = original.substring(original.lastIndexOf('.'));
      }
      String name = UUID.randomUUID() + ext;
      Path target = uploadsDir.resolve(name);
      file.transferTo(target);
      return "/uploads/" + name;
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to upload file");
    }
  }
}
