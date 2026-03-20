package com.ma_sante_assurance.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PageRequest(
    @Min(0) int page,
    @Min(1) @Max(100) int size,
    String sortBy,
    String sortDir
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final String DEFAULT_SORT = "id";
    public static final String DEFAULT_DIR = "asc";

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, DEFAULT_SORT, DEFAULT_DIR);
    }

    public static PageRequest of(int page, int size, String sortBy, String sortDir) {
        return new PageRequest(page, size, sortBy, sortDir);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy != null ? sortBy : DEFAULT_SORT;
    }

    public String getSortDir() {
        return sortDir != null ? sortDir : DEFAULT_DIR;
    }

    public org.springframework.data.domain.PageRequest toSpringPageRequest() {
        org.springframework.data.domain.Sort.Direction direction = 
            "desc".equalsIgnoreCase(getSortDir()) 
                ? org.springframework.data.domain.Sort.Direction.DESC 
                : org.springframework.data.domain.Sort.Direction.ASC;
        return org.springframework.data.domain.PageRequest.of(getPage(), getSize(), direction, getSortBy());
    }
}
