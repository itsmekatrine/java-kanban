package com.yandex.app.test;

import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImmutabilityTaskTest {
    private InMemoryTaskManager manager;
    private Task task;

    @BeforeEach
    public void setup() {
        manager = new InMemoryTaskManager();
        task = new Task(1,"Test addNewTask", "Test addNewTask description");
    }

    @Test
    void shouldImmutabilityTask() {
        manager.createTask(task);

        Task extractedIdOfTask = manager.getTaskById(task.getId());
        assertEquals(task, extractedIdOfTask);

        Task extractedTitleOfTask = manager.getTaskByTitle(task.getTitle());
        assertEquals(task, extractedTitleOfTask);

        Task extractedDescriptionOfTask = manager.getTaskByDescription(task.getDescription());
        assertEquals(task, extractedDescriptionOfTask);
    }
}