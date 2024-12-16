package com.yandex.app.service;

import java.io.File;

public class Managers {
    private static final HistoryManager history = new InMemoryHistoryManager();
    private static final String FILE = "tasks.csv";

    public static TaskManager getDefault() {
        File file = new File(FILE);
        return new FileBackedTaskManager(file, history);
    }

    public static HistoryManager getDefaultHistory() {
        return history;
    }
}
