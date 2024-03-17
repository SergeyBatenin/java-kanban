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
import java.util.stream.Collectors;

public class FileBackedTaskService extends InMemoryTaskService {
    private final File backupFilename;
    private static final String DELIMITER = ",";
    private static final String FILE_HEADER = String.format("id%1$stype%1$sname%1$sstatus%1$sdescription%1$srelated", DELIMITER);

    public FileBackedTaskService(File file) {
        super();
        backupFilename = file;
    }

    public static void main(String[] args) {
        FileBackedTaskService firstBackupService = FileBackedTaskService.loadFromFile(new File("resources/backup.csv"));
        FileBackedTaskService mainService = new FileBackedTaskService(new File("resources/backup.csv"));
        System.out.println("history main manager");
        System.out.println(mainService.getHistory());
        Task task = mainService.createSimpleTask(new Task("main task name", "main task description", TaskStatus.NEW));
        Epic epic = mainService.createEpicTask(new Epic("main epic name", "main epic description", TaskStatus.NEW));
        SubTask subTask = mainService.createSubTask(new SubTask("main subtask name", "main subtask description", TaskStatus.NEW, epic.getId()));
        mainService.getSimpleTaskById(task.getId());
        mainService.getEpicTaskById(epic.getId());
        mainService.getSubTaskById(subTask.getId());
        System.out.println(mainService.getHistory());
        FileBackedTaskService secondBackupService = FileBackedTaskService.loadFromFile(new File("resources/backup.csv"));
        System.out.println(mainService.getHistory().equals(firstBackupService.getHistory()));
        System.out.println(mainService.getHistory().equals(secondBackupService.getHistory()));
    }

    @Override
    public Task createSimpleTask(Task task) {
        Task createdTask = super.createSimpleTask(task);
        save();
        return createdTask;
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        SubTask createdTask = super.createSubTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpicTask(Epic task) {
        Epic createdTask = super.createEpicTask(task);
        save();
        return createdTask;
    }

    @Override
    public Task updateSimpleTask(Task task) {
        Task createdTask = super.updateSimpleTask(task);
        save();
        return createdTask;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        SubTask createdTask = super.updateSubTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic updateEpicTask(Epic task) {
        Epic createdTask = super.updateEpicTask(task);
        save();
        return createdTask;
    }

    @Override
    public void removeAllSimpleTasks() {
        super.removeAllSimpleTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpicTasks() {
        super.removeAllEpicTasks();
        save();
    }

    @Override
    public Task getSimpleTaskById(long id) {
        Task createdTask = super.getSimpleTaskById(id);
        save();
        return createdTask;
    }

    @Override
    public SubTask getSubTaskById(long id) {
        SubTask createdTask = super.getSubTaskById(id);
        save();
        return createdTask;
    }

    @Override
    public Epic getEpicTaskById(long id) {
        Epic createdTask = super.getEpicTaskById(id);
        save();
        return createdTask;
    }

    @Override
    public void removeSimpleTaskById(long id) {
        super.removeSimpleTaskById(id);
        save();
    }

    @Override
    public void removeSubTaskById(long id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeEpicTaskById(long id) {
        super.removeEpicTaskById(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFilename))) {
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
        System.out.println("backup успешно завершен");
    }

    public static FileBackedTaskService loadFromFile(File file) {
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            final FileBackedTaskService fileBackedTaskService = new FileBackedTaskService(file);

            br.readLine(); // пропускаем заголовок
            String line = br.readLine();
            if (line == null) {
                throw new RuntimeException("Файл: " + file + " не содержит данные для восстановления задач.");
            }

            while (line != null && !line.isBlank()) {
                restoreTask(fileBackedTaskService, line);
                line = br.readLine();
            }

            line = br.readLine(); // читаем историю
            if (line == null) {
                throw new RuntimeException("Файл: " + file + " не содержит данные для восстановления истории");
            }
            if (!line.isBlank()) {
                String[] historyTaskIds = line.split(DELIMITER);
                for (String taskId : historyTaskIds) {
                    long id = Long.parseLong(taskId);
                    restoreTaskForHistory(fileBackedTaskService, id);
                }
            }

            line = br.readLine(); // читаем последний id задач
            if (line == null || line.isBlank()) {
                throw new RuntimeException("Файл: \"" + file + "\" не содержит данные для восстановления идентификатора");
            }
            fileBackedTaskService.taskIdentifier = Long.parseLong(line);
            System.out.println("Бэкап успешно восстановлен");

            return fileBackedTaskService;
        } catch (NumberFormatException e) { // написать свои исключения на историю и идентификатор
            throw new RuntimeException("Ошибка при восстановлении идентификатора задачи из файла: " + file, e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при восстановлении бэкапа из файла: " + file, e);
        }
    }

    private static void restoreTask(FileBackedTaskService taskService, String taskInfo) {
        if (taskInfo == null) {
            throw new RuntimeException("Данные для восстановления задачи отсутствуют");
        }
        String[] taskData = taskInfo.split(DELIMITER);
        switch (taskData[1]) {
            case "TASK":
                TaskDto taskDto = TaskMapper.dtoFromData(taskData);
                Task task = TaskMapper.dtoToTask(taskDto);
                taskService.simpleTasks.put(task.getId(), task);
                break;
            case "SUBTASK":
                SubtaskDto subtaskDto = SubtaskMapper.dtoFromData(taskData);
                SubTask subTask = SubtaskMapper.dtoToSubtask(subtaskDto);
                taskService.subTasks.put(subTask.getId(), subTask);
                break;
            case "EPIC":
                EpicDto epicDto = EpicMapper.dtoFromData(taskData);
                Epic epic = EpicMapper.dtoToEpic(epicDto);
                taskService.epicTasks.put(epic.getId(), epic);
                break;
        }
    }

    private static void restoreTaskForHistory(FileBackedTaskService taskService, long id) {
        if (taskService.simpleTasks.containsKey(id)) {
            taskService.historyManager.add(taskService.simpleTasks.get(id));
        } else if (taskService.epicTasks.containsKey(id)) {
            taskService.historyManager.add(taskService.epicTasks.get(id));
        } else if (taskService.subTasks.containsKey(id)) {
            taskService.historyManager.add((taskService.subTasks.get(id)));
        } else {
            throw new RuntimeException("Ошибка восстановления истории. Менеджер не содержит такую задачу");
        }
    }
}