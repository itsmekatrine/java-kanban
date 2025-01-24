package com.yandex.app.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson = new Gson();

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("GET".equals(method) && "/subtasks".equals(path)) {
                handleGetAllSubtasks(exchange);
            } else if ("POST".equals(method) && path.matches("/subtasks/\\d+")) {
                handleCreateSubtask(exchange);
            } else if ("GET".equals(method) && path.matches("/subtasks/\\d+")) {
                handleGetSubtaskById(exchange);
            } else if ("DELETE".equals(method) && path.matches("/subtasks/\\d+")) {
                handleDeleteSubtaskById(exchange);
            } else {
                sendNotFound(exchange, "Endpoint not found");
            }
        } catch (Exception e) {
            sendError(exchange, e.getMessage());
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks), 200);
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        int epicId = extractId(exchange.getRequestURI().getPath());
        String body = new String(exchange.getRequestBody().readAllBytes());
        Subtask subtask = gson.fromJson(body, Subtask.class);

        try {
            int id = taskManager.createSubtask(epicId, subtask);
            sendText(exchange, "{\"id\": " + id + "}", 201);
        } catch (Exception e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        Subtask subtask = taskManager.getSubtaskById(id);

        if (subtask == null) {
            sendNotFound(exchange, "Subtask not found");
        } else {
            sendText(exchange, gson.toJson(subtask), 200);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        int id = extractId(exchange.getRequestURI().getPath());
        boolean deleted = taskManager.deleteSubtaskById(id);

        if (deleted) {
            sendText(exchange, "", 204);
        } else {
            sendNotFound(exchange, "Subtask not found");
        }
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
    }
}