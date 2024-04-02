package service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskServiceTest extends TaskServiceTest<FileBackedTaskService> {
    private static File tempFile;

    @BeforeAll
    static void initFile() throws IOException {
        tempFile = Files.createTempFile(null, null).toFile();
    }

    @Override
    @BeforeEach
    void initializeTaskService() {
        taskService = new FileBackedTaskService(tempFile);
        super.initializeTasks();
    }

    @Test
    @DisplayName("Восстановление TaskService из файла")
    void shouldReturnEqualsTaskService() {
        FileBackedTaskService backupFileTaskService = FileBackedTaskService.loadFromFile(tempFile);

        assertEquals(taskService.getAllSimpleTasks(), backupFileTaskService.getAllSimpleTasks(), "Список задач не совпадает");
        assertEquals(taskService.getAllSubTasks(), backupFileTaskService.getAllSubTasks(), "Список подзадач не совпадает");
        assertEquals(taskService.getAllEpicTasks(), backupFileTaskService.getAllEpicTasks(), "Список эпиков не совпадает");
        assertEquals(taskService.getHistory(), backupFileTaskService.getHistory(), "История задач не совпадает");
        assertEquals(taskService.getPrioritizedTasks(), backupFileTaskService.getPrioritizedTasks(), "История задач не совпадает");
    }
}
