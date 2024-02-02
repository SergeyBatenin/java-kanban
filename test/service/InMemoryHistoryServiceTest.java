package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryServiceTest {

    private HistoryService historyService;

    @BeforeEach
    void initializeHistoryService() {
        historyService = new InMemoryHistoryService();
    }

    @Test
    @DisplayName("Проверка размера списка при добавлении нескольких задач")
    void shouldReturn3AfterAddingThreeTasks() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("name2", "description2", TaskStatus.NEW);
        task1.setId(2);
        Task task3 = new Task("name3", "description3", TaskStatus.NEW);
        task1.setId(3);

        historyService.add(task1);
        historyService.add(task2);
        historyService.add(task3);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(3, historyService.getHistory().size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка размера списка при добавлении максимума задач")
    void shouldReturn10AfterAddingTenTasks() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        task1.setId(1);

        for (int i = 0; i < 10; i++) {
            historyService.add(task1);
        }

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(10, historyService.getHistory().size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка размера списка при добавлении задач больше максимального размера")
    void shouldReturn10AfterAddingTasksAboveMaxSize() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        task1.setId(1);
        List<Task> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            historyService.add(task1);
            list.add(task1);
        }

        Task task2 = new Task("name1", "description1", TaskStatus.NEW);
        task1.setId(2);
        historyService.add(task2);
        list.set(9, task2);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(10, historyService.getHistory().size(), "Размер списка не совпадает");
        assertEquals(list, historyService.getHistory(), "Список истории просмотров не совпадает");
    }
}