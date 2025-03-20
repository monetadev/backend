package com.github.monetadev.backend.graphql.type.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageInfo {
    private int totalPages;
    private long totalElements;
    private int currentPage;
}
