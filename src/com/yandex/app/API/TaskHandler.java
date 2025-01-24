package com.yandex.app.API;

import com.sun.net.httpserver.HttpExchange;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.exception.NotFoundException;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && "/tasks".equals(path)) {
                handleGetAllTasks(exchange);
            } else if ("POST".equals(method) && path.matches("/tasks/?")) {
                handleCreateOrUpdateTask(exchange);
            } else if ("GET".equals(method) && path.matches("/tasks/\\d+")) {
                handleGetTaskById(exchange);
            } else if ("DELETE".equals(method) && path.matches("/tasks/\\d+")) {
                handleDeleteTaskById(exchange);
            } else if ("DELETE".equals(method) && "/tasks".equals(path)) {
                handleDeleteAllTasks(exchange);
            } else {
                sendNotFound(exchange, "Endpoint not found");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, gson.toJson(tasks), 200);
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == null) {
            // Если id не указан, создаем новую задачу
            int id = taskManager.createTask(task);
            sendText(exchange, "{\"id\": " + id + "}", 201);
        } else {
            // Если id указан, обновляем существующую задачу
            try {
                taskManager.updateTask(task.getId(), task);
                sendText(exchange, "{\"message\": \"Task updated successfully\"}", 200);
            } catch (NotFoundException e) {
                sendNotFound(exchange, e.getMessage());
            }
        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        Task task = taskManager.getTaskById(id);
        sendText(exchange, gson.toJson(task), 200);
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        boolean deleted = taskManager.deleteTaskById(id);

        if (deleted) {
            sendText(exchange, "", 204);
        } else {
            throw new NotFoundException("Task with ID " + id + " not found");
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(exchange, "", 204);
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }
}
