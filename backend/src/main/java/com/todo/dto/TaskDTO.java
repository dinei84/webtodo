package com.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para criação e atualização de Tasks.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(min = 1, max = 200, message = "O título deve ter entre 1 e 200 caracteres")
    private String title;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    private String description;

    private Boolean completed;
    private LocalDateTime dueDate;
    private String priority; // LOW, MEDIUM, HIGH
}
