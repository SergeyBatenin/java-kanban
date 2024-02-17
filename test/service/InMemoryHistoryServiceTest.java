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
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW);
        Task task2 = new Task(2, "name2", "description2", TaskStatus.NEW);
        Task task3 = new Task(3, "name3", "description3", TaskStatus.NEW);

        historyService.add(task1);
        historyService.add(task2);
        historyService.add(task3);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(3, historyService.getHistory().size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка размера списка при повторном добавлении одной и той же задачи")
    void shouldReturn1AfterAddingTheSameTask() {
        Task task1 = new Task(1,"name1", "description1", TaskStatus.NEW);

        for (int i = 0; i < 10; i++) {
            historyService.add(task1);
        }

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(1, historyService.getHistory().size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка очередности задач в списке при добавлении задачи, которая уже находится в нем")
    void shouldBeEqualsAfterAddingTaskThatExistsInHistory() {
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW);
        Task task2 = new Task(2, "name1", "description1", TaskStatus.NEW);

        List<Task> list = new ArrayList<>();
        list.add(task2);
        list.add(task1);

        historyService.add(task1);
        historyService.add(task2);
        historyService.add(task1);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(2, historyService.getHistory().size(), "Размер списка не совпадает");
        assertEquals(list, historyService.getHistory(), "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка удаления задачи из истории, которая в ней существует")
    void shouldEqualsAfterDeletingTaskFromHistoryThatExists() {
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW);
        Task task2 = new Task(2, "name1", "description1", TaskStatus.NEW);

        List<Task> list = new ArrayList<>();
        list.add(task2);

        historyService.add(task1);
        historyService.add(task2);
        historyService.remove(1);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(1, historyService.getHistory().size(), "Размер списка не совпадает");
        assertEquals(list, historyService.getHistory(), "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка списка после удаления задачи из середины списка")
    void shouldBeEqualAfterRemovingTaskFromMiddleList() {
        Task task1 = new Task(1, "name1", "description1", TaskStatus.NEW);
        Task task2 = new Task(2, "name2", "description2", TaskStatus.NEW);
        Task task3 = new Task(3, "name3", "description3", TaskStatus.NEW);

        List<Task> list = new ArrayList<>();
        list.add(task1);
        list.add(task3);

        historyService.add(task1);
        historyService.add(task2);
        historyService.add(task3);
        historyService.remove(2);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(2, historyService.getHistory().size(), "Размер списка не совпадает");
        assertEquals(list, historyService.getHistory(), "Список истории просмотров не совпадает");
    }
}