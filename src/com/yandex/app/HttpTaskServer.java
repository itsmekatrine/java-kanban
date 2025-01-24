package com.yandex.app;

import com.sun.net.httpserver.HttpServer;
import com.yandex.app.API.*;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private HttpServer server;
    private TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this.taskManager = Managers.getDefault();
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        System.out.println("Server started on http://localhost:8080/");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }


    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}