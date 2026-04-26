package com.todo.controller;

import com.todo.dto.PageResponse;
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
 * OTIMIZADO: paginacao server-side se > 100 tasks.
 * Todos os endpoints requerem autenticaÃ§ao via Firebase Token.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 100;

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    /**
     * GET /api/tasks - Lista tasks do usuario com paginacao server-side.
     * Se total <= 100: retorna todas as tasks de uma vez.
     * Se total > 100: usa paginacao com page e size.
     */
    @GetMapping
    public ResponseEntity<?> getAllTasks(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        try {
            size = Math.min(size, MAX_PAGE_SIZE);
            TaskService.TaskQueryResult result = taskService.getTasksByUserIdPaginated(
                    user.getUid(), page, size);

            if (!result.isPaginated()) {
                // Sem paginacao: retorna lista simples (retrocompatibilidade)
                return ResponseEntity.ok(result.getTasks());
            }

            // Com paginacao: retorna PageResponse
            PageResponse<Task> response = PageResponse.of(
                    result.getTasks(),
                    result.getPage(),
                    result.getSize(),
                    result.getTotalElements()
            );


            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao buscar tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/tasks/count - Retorna total de tasks do usuario.
     * UTIL: para o frontend saber se deve usar paginacao.
     */
    @GetMapping("/count")
    public ResponseEntity<?> getTaskCount(@AuthenticationPrincipal UserPrincipal user) {
        try {
            long count = taskService.countTasksByUserId(user.getUid());
            return ResponseEntity.ok(java.util.Map.of(
                    "count", count,
                    "paginated", count > 100
            ));
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Erro ao contar tasks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/tasks/{id} - Busca uma task especifica
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
