package service;

import model.Epic;
import model.SubTask;
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
    private Task task = new Task(1, "name", "description", TaskStatus.NEW);
    private Epic epic = new Epic(2, "name", "description", TaskStatus.NEW);
    private SubTask subtask = new SubTask(3, "name", "description", TaskStatus.NEW, 2);

    @BeforeEach
    void initializeHistoryService() {
        historyService = new InMemoryHistoryService();
    }

    @Test
    @DisplayName("Проверка размера списка при добавлении нескольких задач")
    void shouldReturn3AfterAddingThreeTasks() {
        historyService.add(task);
        historyService.add(epic);
        historyService.add(subtask);

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(3, history.size(), "Размер списка не совпадает");
    }

    @Test
    @DisplayName("Проверка размера списка при повторном добавлении одной и той же задачи")
    void shouldReturn1AfterAddingTheSameTask() {
        for (int i = 0; i < 10; i++) {
            historyService.add(task);
        }

        assertNotNull(historyService.getHistory(), "История не существует");
        assertEquals(1, historyService.getHistory().size(), "Размер списка не совпадает");
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
        assertEquals(2, historyService.getHistory().size(), "Размер списка не совпадает");
        assertEquals(exptectedList, historyService.getHistory(), "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка удаления задачи из истории, которая в ней существует")
    void shouldEqualsAfterDeletingTaskFromHistoryThatExists() {
        List<Task> expectedList = List.of(task);

        historyService.add(task);
        historyService.add(subtask);
        historyService.remove(subtask.getId());

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(1, history.size(), "Размер списка не совпадает");
        assertEquals(expectedList, history, "Список истории просмотров не совпадает");
    }

    @Test
    @DisplayName("Проверка списка после удаления задачи из середины списка")
    void shouldBeEqualAfterRemovingTaskFromMiddleList() {
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(task);
        expectedList.add(epic);

        historyService.add(task);
        historyService.add(subtask);
        historyService.add(epic);
        historyService.remove(subtask.getId());

        final List<Task> history = historyService.getHistory();

        assertNotNull(history, "История не существует");
        assertEquals(2, history.size(), "Размер списка не совпадает");
        assertEquals(expectedList, history, "Список истории просмотров не совпадает");
    }
}