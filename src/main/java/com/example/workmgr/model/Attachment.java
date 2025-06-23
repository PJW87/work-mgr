package com.example.workmgr.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Attachment {
    private Long id;             // PK: 첨부파일 고유 ID
    private Long postId;         // FK: posts.id
    private String filename;     // 원본 파일명
    private String url;          // 서버 저장 경로 또는 외부 URL
    private LocalDateTime uploadedAt; // 업로드 일시
    /**
     * 편의 생성자: id와 uploadedAt은 DB 기본값에 맡긴다
     */
    public Attachment(Long postId, String filename, String url) {
        this.postId   = postId;
        this.filename = filename;
        this.url      = url;
    }
}
