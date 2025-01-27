package com.yandex.app.API;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.yandex.app.exception.NotFoundException;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && "/subtasks".equals(path)) {
                handleGetAllSubtasks(exchange);
            } else if ("POST".equals(method) && path.matches("/subtasks/?")) {
                handleCreateOrUpdateSubtask(exchange);
            } else if ("GET".equals(method) && path.matches("/subtasks/\\d+")) {
                handleGetSubtaskById(exchange);
            } else if ("DELETE".equals(method) && path.matches("/subtasks/\\d+")) {
                handleDeleteSubtaskById(exchange);
            } else if ("DELETE".equals(method) && "/subtasks".equals(path)) {
                handleDeleteAllSubtasks(exchange);
            } else {
                sendNotFound(exchange, "Endpoint not found");
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            if (subtask.getId() == null || subtask.getId() == 0) {
                // Создаем новую подзадачу
                int id = taskManager.createSubtask(subtask.getEpicId(), subtask);
                sendText(exchange, "{\"id\": " + id + "}", 201);
            } else {
                // Обновляем подзадачу
                taskManager.updateSubtask(subtask.getId(), subtask);
                sendText(exchange, "{\"message\": \"Subtask updated successfully\"}", 200);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        Subtask subtask = taskManager.getSubtaskById(id); // Если подзадача не найдена, выбросится NotFoundException
        sendText(exchange, gson.toJson(subtask), 200);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        taskManager.deleteSubtaskById(id); // Если подзадача не найдена, выбросится NotFoundException
        sendText(exchange, "", 204);
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        sendText(exchange, "", 204);
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }
}