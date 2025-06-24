// src/main/java/com/example/workmgr/controller/rest/AttachmentRestController.java
package com.example.workmgr.controller.rest;

import com.example.workmgr.mapper.AttachmentMapper;
import com.example.workmgr.model.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attachments")
public class AttRestController {
    private static final String UPLOAD_URL_PREFIX = "/uploads/";
    private static final String UPLOAD_DIR        = "D:/files";  // 실제 파일이 저장된 폴더

    private final AttachmentMapper attMapper;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
        Attachment att = attMapper.findById(id);
        if (att == null) {
            log.warn("Attachment#{} 가 DB에 없습니다.", id);
            return ResponseEntity.notFound().build();
        }

        String stored = att.getStoragePath();
        log.info("Attachment#{} → storagePath='{}'", id, stored);

        if (stored == null || !stored.startsWith("/uploads/")) {
            log.warn("storagePath 가 null 이거나 잘못된 형식입니다. storagePath={}", stored);
            return ResponseEntity.notFound().build();
        }

        String filename = stored.substring("/uploads/".length());
        Path file = Paths.get("D:/files", filename);
        log.info("실제 파일 경로 → {}", file.toAbsolutePath());

        Resource resource = new UrlResource(file.toUri());
        if (!resource.exists() || !resource.isReadable()) {
            log.error("파일이 존재하지 않거나 읽을 수 없습니다: {}", file);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        ContentDisposition cd = ContentDisposition
                .attachment()
                .filename(att.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(att.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .body(resource);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        if (attMapper.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        attMapper.delete(id);
        return ResponseEntity.noContent().build();
    }
}
