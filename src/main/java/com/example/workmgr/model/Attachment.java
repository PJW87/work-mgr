package com.example.workmgr.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class Attachment {
    private Long id;
    private Long postId;     // posts.id FK
    private Long issueId;    // issues.id FK
    private String filename;
    private String storagePath;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadedAt;

    // 기본 생성자 (Lombok @NoArgsConstructor)

    // Post 전용 3-arg 생성자 (contentType, fileSize 없이)
    public Attachment(Long postId, String filename, String storagePath) {
        this.postId      = postId;
        this.filename    = filename;
        this.storagePath = storagePath;
        this.uploadedAt  = LocalDateTime.now();
    }

    // Post 전용 5-arg 생성자
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

    // Issue 전용 정적 팩토리 메서드 (5-arg)
    public static Attachment forIssue(Long issueId,
                                      String filename,
                                      String storagePath,
                                      String contentType,
                                      long fileSize) {
        Attachment a = new Attachment();
        a.issueId     = issueId;
        a.filename    = filename;
        a.storagePath = storagePath;
        a.contentType = contentType;
        a.fileSize    = fileSize;
        a.uploadedAt  = LocalDateTime.now();
        return a;
    }

    // Issue 전용 3-arg 생성자 (contentType, fileSize 없이)
    public static Attachment forIssue(Long issueId, String filename, String storagePath) {
        Attachment a = new Attachment();
        a.issueId     = issueId;
        a.filename    = filename;
        a.storagePath = storagePath;
        a.uploadedAt  = LocalDateTime.now();
        return a;
    }
}
