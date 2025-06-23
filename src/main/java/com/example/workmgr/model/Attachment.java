package com.example.workmgr.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class Attachment {
    private Long id;
    private Long postId;
    private String filename;
    private String storagePath;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadedAt;

    // 이 편의 생성자만 추가해 주면 됩니다.
    public Attachment(Long postId, String filename, String storagePath) {
        this.postId      = postId;
        this.filename    = filename;
        this.storagePath = storagePath;
        this.uploadedAt  = LocalDateTime.now();
    }
    // 새로 추가하는 5-arg 생성자
    public Attachment(Long postId,
                      String filename,
                      String storagePath,
                      String contentType,
                      long fileSize) {
        this.postId      = postId;
        this.filename    = filename;
        this.storagePath = storagePath;
        this.contentType = contentType;
        this.fileSize    = fileSize;
        this.uploadedAt  = LocalDateTime.now();
    }
}
