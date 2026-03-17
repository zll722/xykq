package com.campus.attendance.service.impl;

import com.campus.attendance.config.AppUploadProperties;
import com.campus.attendance.exception.BizException;
import com.campus.attendance.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final Set<String> ALLOWED_EXT = Set.of(
            "jpg", "jpeg", "png", "gif", "webp", "pdf", "doc", "docx"
    );
    private final AppUploadProperties uploadProperties;

    public FileStorageServiceImpl(AppUploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public String save(MultipartFile file, String category) {
        if (file == null || file.isEmpty()) {
            throw new BizException(4001, "上传文件不能为空");
        }
        String original = file.getOriginalFilename();
        String ext = getFileExt(original);
        if (!ALLOWED_EXT.contains(ext.toLowerCase())) {
            throw new BizException(4001, "不支持的文件类型");
        }

        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        Path basePath = Paths.get(uploadProperties.getBaseDir()).normalize().toAbsolutePath();
        Path saveDir = basePath.resolve(sanitize(category)).resolve(month);
        try {
            Files.createDirectories(saveDir);
            String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path target = saveDir.resolve(filename);
            file.transferTo(target);
            return "/uploads/" + sanitize(category) + "/" + month + "/" + filename;
        } catch (IOException e) {
            throw new BizException(5000, "文件保存失败");
        }
    }

    private String getFileExt(String filename) {
        String clean = StringUtils.hasText(filename) ? filename : "";
        int idx = clean.lastIndexOf('.');
        if (idx < 0 || idx == clean.length() - 1) {
            return "";
        }
        return clean.substring(idx + 1);
    }

    private String sanitize(String category) {
        String value = StringUtils.hasText(category) ? category : "common";
        return value.replaceAll("[^a-zA-Z0-9_-]", "");
    }
}
