package com.restaurant.menu.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    T data,
    Metadata metadata
) {
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data, new Metadata(Instant.now(), "1.0"));
    }

    public record Metadata(Instant timestamp, String version) {}
}
