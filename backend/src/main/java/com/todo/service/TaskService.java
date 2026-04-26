package com.todo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.todo.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Service para gerenciar operacoes de Tasks no Firestore.
 * OTIMIZADO: ordenacao feita no Firestore + paginacao server-side + cache Caffeine.
 */
@Service
public class TaskService {


    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private static final String COLLECTION_NAME = "tasks";
    private static final int DEFAULT_PAGE_SIZE = 50;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int PAGINATION_THRESHOLD = 100;

    private final Firestore firestore;
    private final CacheManager cacheManager;

    // Cache local para contar tasks por usuario (TTL curto para nao sobrecarregar Firestore)
    private final Cache<String, Long> taskCountCache;

    public TaskService(Firestore firestore, CacheManager cacheManager) {
        this.firestore = firestore;
        this.cacheManager = cacheManager;
        this.taskCountCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .recordStats()
                .build();
    }


    /**
     * Cria uma nova task no Firestore.
     * INVALIDA cache do usuario.
     */
    @CacheEvict(value = "tasks", key = "'user:' + #task.userId + ':page:' + 0")
    public Task createTask(Task task) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
        task.setId(docRef.getId());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());


        ApiFuture<WriteResult> result = docRef.set(taskToMap(task));
        result.get();

        // Invalida cache de contagem
        taskCountCache.invalidate(getTaskCountKey(task.getUserId()));

        logger.info("Task criada com ID: {} para usuario: {}", task.getId(), task.getUserId());
        return task;
    }

    /**
     * Busca tasks de um usuario com paginacao server-side.
     * Se total <= 100, retorna tudo de uma vez (sem paginacao).
     * Se total > 100, usa paginacao com cursor.
     */
    public TaskQueryResult getTasksByUserIdPaginated(String userId, int page, int size)
            throws ExecutionException, InterruptedException {

        // Verifica total de tasks (com cache)
        long totalTasks = getTaskCountWithCache(userId);

        if (totalTasks <= PAGINATION_THRESHOLD) {
            // Sem paginacao: retorna tudo de uma vez
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Direction.DESCENDING)
                    .get();

            List<Task> tasks = new ArrayList<>();
            for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
                tasks.add(documentToTask(doc));
            }

            logger.debug("Encontradas {} tasks (sem paginacao) para usuario: {}", tasks.size(), userId);
            return TaskQueryResult.fullPage(tasks);
        }

        // Com paginacao: usa offset e limit
        size = Math.min(size, MAX_PAGE_SIZE);
        int offset = page * size;

        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Direction.DESCENDING)
                .offset(offset)
                .limit(size)
                .get();


        List<Task> tasks = new ArrayList<>();
        for (QueryDocumentSnapshot doc : future.get().getDocuments()) {
            tasks.add(documentToTask(doc));
        }

        logger.debug("Encontradas {} tasks (pagina {} de {} total) para usuario: {}",
                tasks.size(), page + 1, totalTasks, userId);

        return TaskQueryResult.paginated(tasks, page, size, totalTasks);
    }

    /**
     * Busca todas as tasks de um usuario especifico (metodo legado para compatibilidade).
     * OTIMIZADO: ordenacao feita no Firestore (nao em memoria).
     * REQUER INDICE COMPOSTO: userId (ASC) + createdAt (DESC)
     */
    public List<Task> getTasksByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Direction.DESCENDING)
                .get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Task> tasks = new ArrayList<>(documents.size());

        for (QueryDocumentSnapshot document : documents) {
            tasks.add(documentToTask(document));
        }

        logger.debug("Encontradas {} tasks para usuario: {}", tasks.size(), userId);
        return tasks;
    }


    /**
     * Conta total de tasks de um usuario.
     */
    public long countTasksByUserId(String userId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .select()
                .get();

        return future.get().getDocuments().size();
    }

    /**
     * Conta tasks com cache local (30s TTL).
     */
    private long getTaskCountWithCache(String userId) throws ExecutionException, InterruptedException {
        String key = getTaskCountKey(userId);
        Long cached = taskCountCache.getIfPresent(key);
        if (cached != null) {
            return cached;
        }

        long count = countTasksByUserId(userId);
        taskCountCache.put(key, count);
        return count;
    }

    private String getTaskCountKey(String userId) {
        return "count:" + userId;
    }

    /**
     * Busca uma task especifica por ID.
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

        if (!task.getUserId().equals(userId)) {
            logger.warn("Tentativa de acesso nao autorizado a task {} por usuario {}", taskId, userId);
            return null;
        }

        return task;
    }

    /**
     * Atualiza uma task existente.
     * INVALIDA cache do usuario.
     */
    @CacheEvict(value = "tasks", key = "'user:' + #userId + ':page:' + 0")
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
        ApiFuture<WriteResult> result = docRef.set(taskToMap(updatedTask), SetOptions.merge());
        result.get();

        // Invalida cache de contagem
        taskCountCache.invalidate(getTaskCountKey(userId));

        logger.info("Task {} atualizada com sucesso", taskId);
        return updatedTask;
    }

    /**
     * Deleta uma task.
     * INVALIDA cache do usuario.
     */
    @CacheEvict(value = "tasks", key = "'user:' + #userId + ':page:' + 0")
    public boolean deleteTask(String taskId, String userId) throws ExecutionException, InterruptedException {
        Task task = getTaskById(taskId, userId);

        if (task == null) {
            return false;
        }

        ApiFuture<WriteResult> result = firestore.collection(COLLECTION_NAME)
                .document(taskId)
                .delete();
        result.get();

        // Invalida cache de contagem
        taskCountCache.invalidate(getTaskCountKey(userId));

        logger.info("Task {} deletada com sucesso", taskId);
        return true;
    }

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

    /**
     * Classe interna para resultado paginado.
     */
    public static class TaskQueryResult {
        private final List<Task> tasks;
        private final int page;
        private final int size;
        private final long totalElements;
        private final boolean paginated;

        private TaskQueryResult(List<Task> tasks, int page, int size, long totalElements, boolean paginated) {
            this.tasks = tasks;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.paginated = paginated;
        }

        public static TaskQueryResult fullPage(List<Task> tasks) {
            return new TaskQueryResult(tasks, 0, tasks.size(), tasks.size(), false);
        }

        public static TaskQueryResult paginated(List<Task> tasks, int page, int size, long totalElements) {
            return new TaskQueryResult(tasks, page, size, totalElements, true);
        }

        public List<Task> getTasks() { return tasks; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public boolean isPaginated() { return paginated; }
    }
}
