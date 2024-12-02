package com.yandex.app.service;

public class Managers {
    private static final HistoryManager history = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(history);
    }

    public static HistoryManager getDefaultHistory() {
        return history;
    }
}
