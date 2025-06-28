package com.example.workmgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 상세 응답용 DTO
 */
@Getter
@AllArgsConstructor
public class IssueDetail {
    private final Issue issue;
    private final List<Attachment> attachments;
}
