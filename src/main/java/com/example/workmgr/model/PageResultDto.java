package com.example.workmgr.model;

import java.util.List;

public class PageResultDto {
    public record PageResult<T>(
            List<T> data, int total, int page, int size
    ){}
}
