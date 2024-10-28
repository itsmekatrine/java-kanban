package com.yandex.app;

import com.yandex.app.model.Task;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Epic;
import com.yandex.app.service.StatusTask;
import com.yandex.app.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = new TaskManager();

        // создание задач
        Task task1 = new Task("com.yandex.app.model.Task 1", "com.yandex.app.model.Task 1");
        int taskId1 = manager.createTask(task1);
        Task task2 = new Task("com.yandex.app.model.Task 2", "com.yandex.app.model.Task 2");
        int taskId2 = manager.createTask(task2);

        // создание эпика № 1
        Epic epic1 = new Epic("com.yandex.app.model.Epic 1", "com.yandex.app.model.Epic 1");
        int epicId1 = manager.createEpic(epic1);

        // создание подзадач к эпику №1
        Subtask subtask1 = new Subtask("com.yandex.app.model.Subtask 1", "com.yandex.app.model.Subtask 1", epicId1);
        int subtaskId1 = manager.createSubtask(subtask1);

        // создание эпика № 2
        Epic epic2 = new Epic("com.yandex.app.model.Epic 2", "com.yandex.app.model.Epic 2");
        int epicId2 = manager.createEpic(epic2);

        // создание подзадач к эпику №2
        Subtask subtask2 = new Subtask("com.yandex.app.model.Subtask 2", "com.yandex.app.model.Subtask 2", epicId2);
        Subtask subtask3 = new Subtask("com.yandex.app.model.Subtask 3", "com.yandex.app.model.Subtask 3", epicId2);
        int subtaskId2 = manager.createSubtask(subtask2);
        int subtaskId3 = manager.createSubtask(subtask3);

        // добавление подзадач в разные эпики
        epic1.addSubtask(subtask1);
        epic2.addSubtask(subtask2);
        epic2.addSubtask(subtask3);

        // вывод списка эпиков, задач, подзадач
        System.out.println("List of epics — " + manager.getAllEpics());
        System.out.println("List of tasks — " + manager.getAllTasks());
        System.out.println("List of subtasks — " + manager.getAllSubtasks());

        System.out.println();

        System.out.println("Current epic №1 status: " + epic1.getStatus());
        System.out.println("Current epic №2 status: " + epic2.getStatus());

        System.out.println();

        // изменение статуса одной из подзадач
        System.out.println("Current subtask №2 status: " + subtask2.getStatus());
        subtask2.setStatus(StatusTask.IN_PROGRESS);
        manager.updateSubtask(subtaskId2, subtask2);
        manager.updateEpic(epicId2, epic2);

        System.out.println("Updated subtask №2 status: " + subtask2.getStatus());
        System.out.println("Updated epic №2 status: " + epic2.managerStatus());

        System.out.println();

        // удаление
        System.out.println("Delete epic №1");
        manager.deleteEpicById(epicId1);
        System.out.println("Delete task №1");
        manager.deleteTaskById(taskId1);
    }
}