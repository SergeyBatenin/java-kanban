package service;

import dto.EpicDto;
import dto.SubtaskDto;
import dto.TaskDto;
import mapper.EpicMapper;
import mapper.SubtaskMapper;
import mapper.TaskMapper;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskService extends InMemoryTaskService {
    private static final String BACKUP_FILENAME = "resources/backup.csv";
    private static final String DELIMITER = ",";
    private static final String FILE_HEADER = String.format("id%1$stype%1$sname%1$sstatus%1$sdescription%1$srelated", DELIMITER);
    private static final int MIN_ROWS_IN_BACKUP = 4;

    public FileBackedTaskService() {
        super();
        load();
    }

    public static void main(String[] args) {
        FileBackedTaskService f = new FileBackedTaskService();
        System.out.println(f.getHistory());
        Task task = f.createSimpleTask(new Task("task name", "task description", TaskStatus.NEW));
        Epic epic = f.createEpicTask(new Epic("epic name", "epic description", TaskStatus.NEW));
        SubTask subTask = f.createSubTask(new SubTask("subtask name", "subtask description", TaskStatus.NEW, epic.getId()));
        f.getSimpleTaskById(task.getId());
        f.getEpicTaskById(epic.getId());
        f.getSubTaskById(subTask.getId());
        System.out.println(f.getHistory());
        f.save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BACKUP_FILENAME))) {
            writer.write(FILE_HEADER);
            writer.newLine();
            TaskDto taskDto;
            for (Task task : simpleTasks.values()) {
                taskDto = TaskMapper.taskToDto(task);
                writer.write(TaskMapper.dtoToString(taskDto));
                writer.newLine();
            }
            EpicDto epicDto;
            for (Epic epic : epicTasks.values()) {
                epicDto = EpicMapper.epicToDto(epic);
                writer.write(EpicMapper.dtoToString(epicDto));
                writer.newLine();
            }
            SubtaskDto subtaskDto;
            for (SubTask subTask : subTasks.values()) {
                subtaskDto = SubtaskMapper.subtaskToDto(subTask);
                writer.write(SubtaskMapper.dtoToString(subtaskDto));
                writer.newLine();
            }
            writer.newLine();
            String historyIdTasks = getHistory().stream()
                    .map(task -> String.valueOf(task.getId()))
                    .collect(Collectors.joining(DELIMITER));
            writer.write(historyIdTasks);
            writer.newLine();
            writer.write(String.valueOf(taskIdentifier));
            writer.flush();
        } catch (IOException e) {
            System.out.println("Запись бэкапа не удалась");
            throw new RuntimeException(e);
        }
        System.out.println("backup сделан");
    }

    private void load() {
        try (FileReader reader = new FileReader(BACKUP_FILENAME, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {


            List<String> backup = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                backup.add(line);
            }
            if (backup.size() < MIN_ROWS_IN_BACKUP) return;

            int limitTasks = backup.size() - 3;
            for (int i = 1; i < limitTasks; i++) {
                restoreTask(backup.get(i));
            }

            String historyTasksLine = backup.get(limitTasks + 1);
            if (!historyTasksLine.isEmpty()) {
                String[] historyTaskIds = historyTasksLine.split(DELIMITER);

                for (String taskId : historyTaskIds) {
                    long id = Long.parseLong(taskId);
                    restoreTaskForHistory(id);
                }
            }

            taskIdentifier = Long.parseLong(backup.get(limitTasks + 2));
            System.out.println("Бэкап успешно восстановлен");
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при восстановлении бэкапа из файла: " + BACKUP_FILENAME, e);
        }
    }

    private void restoreTask(String taskInfo) {
        String[] taskData = taskInfo.split(DELIMITER);
        switch (taskData[1]) {
            case "TASK":
                TaskDto taskDto = TaskMapper.dtoFromData(taskData);
                Task task = TaskMapper.dtoToTask(taskDto);
                simpleTasks.put(task.getId(), task);
                break;
            case "SUBTASK":
                SubtaskDto subtaskDto = SubtaskMapper.dtoFromData(taskData);
                SubTask subTask = SubtaskMapper.dtoToSubtask(subtaskDto);
                subTasks.put(subTask.getId(), subTask);
                break;
            case "EPIC":
                EpicDto epicDto = EpicMapper.dtoFromData(taskData);
                Epic epic = EpicMapper.dtoToEpic(epicDto);
                epicTasks.put(epic.getId(), epic);
                break;
        }
    }

    private void restoreTaskForHistory(long id) {
        if (simpleTasks.containsKey(id)) {
            historyManager.add(simpleTasks.get(id));
        } else if (epicTasks.containsKey(id)) {
            historyManager.add(epicTasks.get(id));
        } else {
            historyManager.add((subTasks.get(id)));
        }
    }
}