package com.example.workmgr.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Board {
    private Long id;             // PK: 게시판 고유 ID
    private Long projectId;      // FK: projects.id
    private String name;         // 게시판 이름 (예: “이슈”, “자유” 등)
    private String description; // 설명
    private LocalDateTime createdAt; // 생성 일시
    private LocalDateTime updatedAt; // 수정 일시
}
