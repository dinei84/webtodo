package com.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para resposta paginada de Tasks.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean paginated; // indica se a resposta estÃ¡ paginada (> 100 tasks)

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponse<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            page < totalPages - 1,
            page > 0,
            totalElements > 100 // sÃ³ paginado se mais de 100 tasks
        );
    }

    public static <T> PageResponse<T> singlePage(List<T> content) {
        return new PageResponse<>(
            content,
            0,
            content.size(),
            content.size(),
            1,
            false,
            false,
            false
        );
    }
}
