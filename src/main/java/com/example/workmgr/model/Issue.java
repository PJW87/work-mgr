// src/main/java/com/example/workmgr/model/Issue.java
package com.example.workmgr.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Issue {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}