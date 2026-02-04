package com.todo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model da Task (Tarefa).
 * Representa uma tarefa no sistema de Todo List.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String id;
    private String userId;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private String priority; // LOW, MEDIUM, HIGH

    public Task(String userId, String title, String description) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.priority = "MEDIUM";
    }
}
