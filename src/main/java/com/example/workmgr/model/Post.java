package com.example.workmgr.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Post {
    private Long id;             // PK: 게시글 고유 ID
    private Long boardId;        // FK: boards.id
    private String author;       // 작성자
    private String title;        // 제목
    private String content;      // 본문
    private LocalDateTime createdAt; // 작성 일시
    private LocalDateTime updatedAt; // 수정 일시
}
