package com.yandex.app.service;

import com.yandex.app.exception.ManagerSaveException;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private HistoryManager history;

    public FileBackedTaskManager(File file, HistoryManager history) {
        super(history);
        this.file = file;
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public int createSubtask(int epicId, Subtask subtask) {
        int id = super.createSubtask(epicId, subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(int id, Task task) {
        super.updateTask(id, task);
        save();
    }

    @Override
    public void updateEpic(int id, Epic epic) {
        super.updateEpic(id, epic);
        save();
    }

    @Override
    public void updateSubtask(int id, Subtask subtask) {
        super.updateSubtask(id, subtask);
        save();
    }

    @Override
    public boolean deleteTaskById(int id) {
        boolean result = super.deleteTaskById(id);
        save();
        return result;
    }

    @Override
    public boolean deleteSubtaskById(int id) {
        boolean result = super.deleteSubtaskById(id);
        save();
        return result;
    }

    @Override
    public boolean deleteEpicById(int id) {
        boolean result = super.deleteEpicById(id);
        save();
        return result;
    }

    public void save() {
        File file = new File("tasks.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : getAllTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString());
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file.getAbsolutePath(), e);
        }
    }

    public String toString(Task task) {
        return task.toString();
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        String status = parts[3];
        String description = parts[4];

        switch (type) {
            case TASK:
                return new Task(id, title, description);
            case EPIC:
                return new Epic(id, title, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                return new Subtask(id, title, description, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager history) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file, history);

        try {
            System.out.println("Absolute path: " + file.getAbsolutePath());
            String strings = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String[] lines = strings.split(System.lineSeparator());
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                System.out.println("Reading line: " + line);
                Task task = fromString(line);
                switch (task.getType()) {
                    case EPIC:
                        manager.createEpic((Epic) task);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.createSubtask(subtask.getEpicId(), subtask);
                        break;
                    case TASK:
                        manager.createTask(task);
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип: " + task.getType());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка чтения файла: " + e.getMessage());
        }
        return manager;
    }
}
