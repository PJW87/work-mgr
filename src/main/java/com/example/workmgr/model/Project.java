package com.example.workmgr.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Project {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    // getters & setters
}
