package com.yandex.app.service;

public class Managers {
    private static final TaskManager manager = new InMemoryTaskManager();
    private static final HistoryManager history = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return manager;
    }

    public static HistoryManager getDefaultHistory() {
        return history;
    }
}
