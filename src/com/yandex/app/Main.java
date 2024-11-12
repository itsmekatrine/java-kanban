package com.yandex.app;

import com.yandex.app.model.Task;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Epic;
import com.yandex.app.service.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager manager = Managers.getDefault();

        // создание задач
        Task task1 = new Task(1,"Task 1", "Task 1");
        int taskId1 = manager.createTask(task1);
        Task task2 = new Task(1,"Task 2", "Task 2");
        int taskId2 = manager.createTask(task2);

        // создание эпика № 1
        Epic epic1 = new Epic(1,"Epic 1", "Epic 1");
        int epicId1 = manager.createEpic(epic1);

        // создание подзадач к эпику №1
        Subtask subtask1 = new Subtask(1,"Subtask 1", "Subtask 1", epicId1);
        int subtaskId1 = manager.createSubtask(epicId1, subtask1);

        // создание эпика № 2
        Epic epic2 = new Epic(2,"Epic 2", "Epic 2");
        int epicId2 = manager.createEpic(epic2);

        // создание подзадач к эпику №2
        Subtask subtask2 = new Subtask(2,"Subtask 2", "Subtask 2", epicId2);
        Subtask subtask3 = new Subtask(2,"Subtask 3", "Subtask 3", epicId2);
        int subtaskId2 = manager.createSubtask(epicId2, subtask2);
        int subtaskId3 = manager.createSubtask(epicId2, subtask3);

        // вывод списка эпиков, задач, подзадач
        System.out.println("List of epics — " + manager.getAllEpics());
        System.out.println("List of tasks — " + manager.getAllTasks());
        System.out.println("List of subtasks — " + manager.getAllSubtasks());

        System.out.println();

        // вывод списка подзадач эпика №2
        System.out.println("List subtasks of epic №2 — " + manager.getAllSubtasksOfEpic(epicId2));

        System.out.println();

        System.out.println("Current epic №1 status: " + epic1.getStatus());
        System.out.println("Current epic №2 status: " + epic2.getStatus());

        System.out.println();

        // изменение статуса одной из подзадач
        System.out.println("Current subtask №2 status: " + subtask2.getStatus());
        subtask2.setStatus(StatusTask.IN_PROGRESS);
        manager.updateSubtask(subtaskId2, subtask2);
        manager.updateEpic(epicId2, epic2);

        System.out.println("Current subtask №3 status: " + subtask3.getStatus());
        subtask3.setStatus(StatusTask.NEW);
        manager.updateSubtask(subtaskId3, subtask3);
        manager.updateEpic(epicId2, epic2);

        System.out.println();

        System.out.println("Updated subtask №2 status: " + subtask2.getStatus());
        System.out.println("Updated subtask №3 status: " + subtask3.getStatus());
        System.out.println("Updated epic №2 status: " + epic2.updateEpicStatus());

        System.out.println();

        // удаление подзадач эпика № 2
        System.out.println("Delete subtasks of epic №2");
        manager.deleteAllSubtasks();
        manager.updateEpic(epicId2, epic2);
        System.out.println("Updated list subtasks of epic №2: " + epic2.getSubtasks());

        System.out.println();

        // удаление эпика № 2
        System.out.println("List of epics — " + manager.getAllEpics());
        manager.deleteEpicById(epicId2);
        manager.updateEpic(epicId2, epic2);
        System.out.println("Update list of epics — " + manager.getAllEpics());
        System.out.println("Updated list subtasks of epic №2: " + epic2.getSubtasks());

        // напечатать историю
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        HistoryManager history = Managers.getDefaultHistory();

        // добавление в историю просмотров
        System.out.println("Add in history — " + manager.getAllTasks());
        history.add(manager.getTaskById(1));
        history.add(manager.getTaskById(2));


        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getAllSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : history.getHistory()) {
            System.out.println(task);
        }
    }
}