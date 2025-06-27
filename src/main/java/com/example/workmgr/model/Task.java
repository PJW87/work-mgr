package com.example.workmgr.model;

import lombok.*;
import java.time.LocalDateTime;

/**
 * 업무(Task) 모델
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    /** PK: 자동증가 */
    private Long id;

    /** 연관된 프로젝트 ID (null 허용) */
    private Long projectId;

    /** 업무 제목 */
    private String title;

    /** 상세 설명 */
    private String description;

    /** 담당자 이름 또는 ID */
    private String assignee;

    /** 현재 상태: OPEN, IN_PROGRESS, IN_REVIEW, DONE, CANCELLED 등 */
    private String status;

    /** 우선순위: LOW, MEDIUM, HIGH, CRITICAL */
    private String priority;

    /** 업무 시작일시 */
    private LocalDateTime startDate;

    /** 업무 마감기한 (null 가능) */
    private LocalDateTime dueDate;

    /** 생성 시각 (DB 자동 채움) */
    private LocalDateTime createdAt;

    /** 최종 수정 시각 (DB 자동 채움) */
    private LocalDateTime updatedAt;

    private String projectName;
}