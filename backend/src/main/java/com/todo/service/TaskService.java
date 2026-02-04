package com.todo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.todo.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Service para gerenciar operações de Tasks no Firestore.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static final String COLLECTION_NAME = "tasks";

    private final Firestore firestore;

    public TaskService(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Cria uma nova task no Firestore.
     */
    public Task createTask(Task task) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
        task.setId(docRef.getId());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        ApiFuture<WriteResult> result = docRef.set(taskToMap(task));
        result.get(); // Aguarda a conclusão

        logger.info("Task criada com ID: {} para usuário: {}", task.getId(), task.getUserId());
        return task;
    }

    /**
     * Busca todas as tasks de um usuário específico.
     */
    public List<Task> getTasksByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Task> tasks = new ArrayList<>();

        for (QueryDocumentSnapshot document : documents) {
            tasks.add(documentToTask(document));
        }

        // Ordena em memória por createdAt (mais recente primeiro)
        tasks.sort((t1, t2) -> {
            if (t1.getCreatedAt() == null && t2.getCreatedAt() == null)
                return 0;
            if (t1.getCreatedAt() == null)
                return 1;
            if (t2.getCreatedAt() == null)
                return -1;
            return t2.getCreatedAt().compareTo(t1.getCreatedAt());
        });

        logger.debug("Encontradas {} tasks para usuário: {}", tasks.size(), userId);
        return tasks;
    }

    /**
     * Busca uma task específica por ID.
     * Retorna null se a task não existir ou não pertencer ao usuário.
     */
    public Task getTaskById(String taskId, String userId) throws ExecutionException, InterruptedException {
        DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                .document(taskId)
                .get()
                .get();

        if (!document.exists()) {
            return null;
        }

        Task task = documentToTask(document);

        // Verifica se a task pertence ao usuário
        if (!task.getUserId().equals(userId)) {
            logger.warn("Tentativa de acesso não autorizado à task {} por usuário {}", taskId, userId);
            return null;
        }

        return task;
    }

    /**
     * Atualiza uma task existente.
     */
    public Task updateTask(String taskId, String userId, Task updatedTask)
            throws ExecutionException, InterruptedException {

        Task existingTask = getTaskById(taskId, userId);

        if (existingTask == null) {
            return null;
        }

        updatedTask.setId(taskId);
        updatedTask.setUserId(userId);
        updatedTask.setCreatedAt(existingTask.getCreatedAt());
        updatedTask.setUpdatedAt(LocalDateTime.now());

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(taskId);
        ApiFuture<WriteResult> result = docRef.set(taskToMap(updatedTask));
        result.get();

        logger.info("Task {} atualizada com sucesso", taskId);
        return updatedTask;
    }

    /**
     * Deleta uma task.
     */
    public boolean deleteTask(String taskId, String userId) throws ExecutionException, InterruptedException {
        Task task = getTaskById(taskId, userId);

        if (task == null) {
            return false;
        }

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(taskId)
                .delete();
        result.get();

        logger.info("Task {} deletada com sucesso", taskId);
        return true;
    }

    /**
     * Converte Task para Map (para salvar no Firestore).
     */
    private Map<String, Object> taskToMap(Task task) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", task.getUserId());
        map.put("title", task.getTitle());
        map.put("description", task.getDescription());
        map.put("completed", task.isCompleted());
        map.put("createdAt", task.getCreatedAt() != null ? task.getCreatedAt().toString() : null);
        map.put("updatedAt", task.getUpdatedAt() != null ? task.getUpdatedAt().toString() : null);
        map.put("dueDate", task.getDueDate() != null ? task.getDueDate().toString() : null);
        map.put("priority", task.getPriority());
        return map;
    }

    /**
     * Converte DocumentSnapshot para Task.
     */
    private Task documentToTask(DocumentSnapshot document) {
        Task task = new Task();
        task.setId(document.getId());
        task.setUserId(document.getString("userId"));
        task.setTitle(document.getString("title"));
        task.setDescription(document.getString("description"));
        task.setCompleted(Boolean.TRUE.equals(document.getBoolean("completed")));

        String createdAt = document.getString("createdAt");
        if (createdAt != null) {
            task.setCreatedAt(LocalDateTime.parse(createdAt));
        }

        String updatedAt = document.getString("updatedAt");
        if (updatedAt != null) {
            task.setUpdatedAt(LocalDateTime.parse(updatedAt));
        }

        String dueDate = document.getString("dueDate");
        if (dueDate != null) {
            task.setDueDate(LocalDateTime.parse(dueDate));
        }

        task.setPriority(document.getString("priority"));

        return task;
    }
}
