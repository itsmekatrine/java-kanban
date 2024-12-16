package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void updateHistory(Task task);

    void remove(int id);

    void clearHistory();

    Task getCurrentTask(int id);

    Task getLastSavedTask(int id);
}
