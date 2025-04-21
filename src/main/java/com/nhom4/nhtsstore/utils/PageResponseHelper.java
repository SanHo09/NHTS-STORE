package com.nhom4.nhtsstore.utils;

import com.nhom4.nhtsstore.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PageResponseHelper {

    public static <T> PageResponse<T> createPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .content(page.getContent())
                .build();
    }

    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .page(0)
                .size(0)
                .totalElements(0)
                .totalPages(0)
                .content(null)
                .build();
    }

    public static Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        return org.springframework.data.domain.PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? org.springframework.data.domain.Sort.by(sortBy).ascending() :
                        org.springframework.data.domain.Sort.by(sortBy).descending());
    }
}
