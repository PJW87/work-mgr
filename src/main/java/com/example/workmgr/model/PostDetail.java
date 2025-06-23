// src/main/java/com/example/workmgr/model/PostDetail.java
package com.example.workmgr.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 게시글 상세 응답용 DTO
 */
@Getter
@AllArgsConstructor
public class PostDetail {
    private final Post post;
    private final List<Attachment> attachments;
}
