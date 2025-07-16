package com.example.workmgr.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuickLink {
    private Long id;
    private String title;
    private String url;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
