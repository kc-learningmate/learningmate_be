package org.kc5.learningmate.api.v1.dto.response.common;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(List<T> items, int page, int size, boolean hasNext,
                           long totalElements, int totalPages) {
    public static <T> PageResponse<T> from(Page<T> p) {
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.hasNext(),
                p.getTotalElements(), p.getTotalPages());
    }
}
