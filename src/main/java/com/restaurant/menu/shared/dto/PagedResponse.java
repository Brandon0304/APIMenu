package com.restaurant.menu.shared.dto;

import java.util.List;

public record PagedResponse<T>(
    List<T> data,
    Pagination pagination
) {
    public record Pagination(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean sorted
    ) {}
}
