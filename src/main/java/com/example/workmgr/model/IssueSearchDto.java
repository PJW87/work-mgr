// src/main/java/com/example/workmgr/model/IssueSearchDto.java
package com.example.workmgr.model;

import lombok.Data;

@Data
public class IssueSearchDto {
    private String type;      // "T", "TC" 등 (T=title, C=content, TC=title+content)
    private String keyword;   // 검색어
    private String from;      // "YYYY-MM-DD"
    private String to;        // "YYYY-MM-DD"
    private int page = 0;
    private int size = 20;
}
