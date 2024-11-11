package com.yandex.app.service;

public class Managers {
    private static final TaskManager manager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
