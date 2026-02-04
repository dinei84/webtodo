package com.todo.controller;

import com.todo.dto.TaskDTO;
import com.todo.model.Task;
import com.todo.security.UserPrincipal;
import com.todo.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Controller REST para gerenciar Tasks.
 * Todos os endpoints requerem autenticação via Firebase Token.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * GET /api/tasks - Lista todas as tasks do usuário autenticado
     */
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@AuthenticationPrincipal UserPrincipal user) {
        try {
            List<Task> tasks = taskService.getTasksByUserId(user.getUid());
            return ResponseEntity.ok(tasks);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao buscar tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/tasks/{id} - Busca uma task específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id,
            @AuthenticationPrincipal UserPrincipal user) {
        try {
            Task task = taskService.getTaskById(id, user.getUid());

            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(task);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao buscar task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/tasks - Cria uma nova task
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserPrincipal user) {
        try {
            Task task = new Task();
            task.setUserId(user.getUid());
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setCompleted(taskDTO.getCompleted() != null ? taskDTO.getCompleted() : false);
            task.setDueDate(taskDTO.getDueDate());
            task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : "MEDIUM");

            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao criar task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PUT /api/tasks/{id} - Atualiza uma task existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id,
            @Valid @RequestBody TaskDTO taskDTO,
            @AuthenticationPrincipal UserPrincipal user) {
        try {
            Task task = new Task();
            task.setTitle(taskDTO.getTitle());
            task.setDescription(taskDTO.getDescription());
            task.setCompleted(taskDTO.getCompleted() != null ? taskDTO.getCompleted() : false);
            task.setDueDate(taskDTO.getDueDate());
            task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : "MEDIUM");

            Task updatedTask = taskService.updateTask(id, user.getUid(), task);

            if (updatedTask == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updatedTask);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao atualizar task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /api/tasks/{id} - Deleta uma task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id,
            @AuthenticationPrincipal UserPrincipal user) {
        try {
            boolean deleted = taskService.deleteTask(id, user.getUid());

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.noContent().build();
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao deletar task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
