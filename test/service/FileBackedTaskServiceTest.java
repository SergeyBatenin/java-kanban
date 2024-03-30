package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskServiceTest {

    private FileBackedTaskService taskService;
    private File tempFile;
    private Task task;
    private Epic epic;
    private SubTask subtask;

    @BeforeEach
    void initializeTaskService() throws IOException {
        tempFile = Files.createTempFile(null, null).toFile();
        taskService = new FileBackedTaskService(tempFile);
        initializeTasks();
    }

    private void initializeTasks() {
        task = new Task(
                "name",
                "description",
                TaskStatus.NEW,
//                LocalDateTime.of(2024, 3, 30, 17, 30),
                null,
                Duration.ofMinutes(15));
        taskService.createSimpleTask(task);
        epic = new Epic("name", "description", TaskStatus.NEW, null, Duration.ZERO);
        taskService.createEpicTask(epic);
        subtask = new SubTask(
                "name",
                "description",
                TaskStatus.NEW,
                epic.getId(),
//                LocalDateTime.of(2024, 3, 29, 17, 30),
                null,
                Duration.ofMinutes(15));
        taskService.createSubTask(subtask);

        taskService.getEpicTaskById(epic.getId());
        taskService.getSimpleTaskById(task.getId());
        taskService.getSubTaskById(subtask.getId());
    }

    @Test
    @DisplayName("Восстановление TaskService из файла")
    void shouldReturnEqualsTaskService() {
        FileBackedTaskService backupFileTaskService = FileBackedTaskService.loadFromFile(tempFile);

        assertEquals(taskService.getAllSimpleTasks(), backupFileTaskService.getAllSimpleTasks(), "Список задач не совпадает");
        assertEquals(taskService.getAllSubTasks(), backupFileTaskService.getAllSubTasks(), "Список подзадач не совпадает");
        assertEquals(taskService.getAllEpicTasks(), backupFileTaskService.getAllEpicTasks(), "Список эпиков не совпадает");
        assertEquals(taskService.getHistory(), backupFileTaskService.getHistory(), "История задач не совпадает");
    }
}
