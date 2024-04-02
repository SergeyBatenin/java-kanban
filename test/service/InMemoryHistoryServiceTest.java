package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryServiceTest {

    private HistoryService historyService;
    private Task task = new Task(
            1,
            "name",
            "description",
            TaskStatus.NEW,
            LocalDateTime.of(2024, 3, 30, 17, 30),
            Duration.ofMinutes(15));
    private Epic epic = new Epic(2, "name", "description", TaskStatus.NEW, null, Duration.ZERO);
    private SubTask subtask = new SubTask(
            3,
            "name",
            "description",
            TaskStatus.NEW,
            2,
            LocalDateTime.of(2024, 3, 29, 17, 30),
            Duration.ofMinutes(15));

    @BeforeEach
    void initializeHistoryService() {
        historyService = new InMemoryHistoryService();
    }

    @Test
    @DisplayName("Получение пустого списка задач")
    void shouldReturnEmptyTaskList() {
        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertTrue(history.isEmpty(), "Список задач не пустой");
    }

    @Test
    @DisplayName("Проверка размера списка при добавлении нескольких задач")
    void shouldReturn3AfterAddingThreeTasks() {
        historyService.add(task);
        historyService.add(epic);
        historyService.add(subtask);
        int expectedSize = 3;

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(expectedSize, history.size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка размера списка при повторном добавлении одной и той же задачи")
    void shouldReturn1AfterAddingTheSameTask() {
        for (int i = 0; i < 10; i++) {
            historyService.add(task);
        }
        int expectedSize = 1;

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(expectedSize, historyService.getHistory().size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка очередности задач в списке при добавлении задачи, которая уже находится в нем")
    void shouldBeEqualsAfterAddingTaskThatExistsInHistory() {
        List<Task> exptectedList = new ArrayList<>();
        exptectedList.add(task);
        exptectedList.add(subtask);

        historyService.add(subtask);
        historyService.add(task);
        historyService.add(subtask);

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(exptectedList.size(), historyService.getHistory().size(), "Размер списка не совпадает");
        assertEquals(exptectedList, historyService.getHistory(), "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка удаления задачи из истории из начала списка")
    void shouldBeEqualAfterRemovingTaskFromBeginList() {
        List<Task> expectedList = List.of(task, epic);

        historyService.add(subtask);
        historyService.add(task);
        historyService.add(epic);
        historyService.remove(subtask.getId());

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(expectedList.size(), history.size(), "Размер списка не совпадает");
        assertEquals(expectedList, history, "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка списка после удаления задачи из середины списка")
    void shouldBeEqualAfterRemovingTaskFromMiddleList() {
        List<Task> expectedList = List.of(task, epic);

        historyService.add(task);
        historyService.add(subtask);
        historyService.add(epic);
        historyService.remove(subtask.getId());

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(expectedList.size(), history.size(), "Размер списка не совпадает");
        assertEquals(expectedList, history, "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка удаления задачи из историис конца списка")
    void shouldBeEqualAfterRemovingTaskFromEndList() {
        List<Task> expectedList = List.of(task, epic);

        historyService.add(task);
        historyService.add(epic);
        historyService.add(subtask);
        historyService.remove(subtask.getId());

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(expectedList.size(), history.size(), "Размер списка не совпадает");
        assertEquals(expectedList, history, "Список истории просмотров не совпадает");
    }
}