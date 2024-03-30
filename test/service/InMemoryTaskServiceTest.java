package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskServiceTest {
    private static final long INCORRECT_ID = -1L;
    private TaskService taskService;
    private Task task;
    private Epic epic;
    private SubTask subtask;

    @BeforeEach
    void initializeTaskService() {
        taskService = new InMemoryTaskService();
        initializeTasks();
    }

    @AfterEach
    void clear() {
        taskService.removeAllSimpleTasks();
        taskService.removeAllEpicTasks();
    }

    private void initializeTasks() {
        task = new Task(
                "name",
                "description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        taskService.createSimpleTask(task);
        epic = new Epic("name", "description", TaskStatus.NEW, null, Duration.ZERO);
        taskService.createEpicTask(epic);
        subtask = new SubTask(
                "name",
                "description",
                TaskStatus.NEW,
                epic.getId(),
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));
        taskService.createSubTask(subtask);
    }

    @Test
    @DisplayName("Создание новой задачи")
    void shouldCreateNewTask() {
        final Task savedTask = taskService.getSimpleTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание новой подзадачи")
    void shouldCreateNewSubTask() {
        final SubTask savedTask = taskService.getSubTaskById(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание нового эпика")
    void shouldCreateNewEpic() {
        final Epic savedTask = taskService.getEpicTaskById(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление задачи")
    void shouldBeEqualsTaskAfterUpdate() {
        task.setDescription("NEW description");
        taskService.updateSimpleTask(task);
        final Task savedTask = taskService.getSimpleTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление подзадачи")
    void shouldBeEqualsSubTaskAfterUpdate() {
        subtask.setDescription("NEW description");
        taskService.updateSubTask(subtask);

        final SubTask savedTask = taskService.getSubTaskById(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление эпика")
    void shouldBeEqualsEpicAfterUpdateField() {
        epic.setDescription("NEW description");
        taskService.updateEpicTask(epic);
        final Epic savedTask = taskService.getEpicTaskById(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика при изменении подзадач")
    void shouldBeEqualsEpicStatusIsProgressAfterUpdateSubtask() {
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateSubTask(subtask);
        final Epic inProgressEpic = taskService.getEpicTaskById(epic.getId());
        assertNotNull(inProgressEpic, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, inProgressEpic.getStatus(), "Статусы не совпадают.");

        subtask.setStatus(TaskStatus.DONE);
        taskService.updateSubTask(subtask);
        final Epic doneEpic = taskService.getEpicTaskById(epic.getId());
        assertNotNull(doneEpic, "Задача не найдена.");
        assertEquals(TaskStatus.DONE, doneEpic.getStatus(), "Статусы не совпадают.");

        taskService.removeAllSubTasks();
        final Epic finalEpic = taskService.getEpicTaskById(epic.getId());
        assertNotNull(finalEpic, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, finalEpic.getStatus(), "Статусы не совпадают.");
    }

    @Test
    @DisplayName("Создание подзадачи с некорректным родительским эпиком")
    void shouldBeThrownRuntimeExceptionWhenCreatingSubtaskWithIncorrectEpic() {
        subtask.setEpicId(INCORRECT_ID);
        
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            taskService.createSubTask(subtask);
        });
        assertEquals("Подзадача не может существовать без эпика", thrown.getMessage());
    }

    @Test
    @DisplayName("Обновление задач с некорректным id")
    void shouldBeThrownRuntimeExceptionWhenUpdatingTasksWithIncorrectID() {
        task.setId(INCORRECT_ID);
        subtask.setEpicId(INCORRECT_ID);
        epic.setId(INCORRECT_ID);

        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateSimpleTask(task);
        });
        assertEquals("Такой задачи не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateSubTask(subtask);
        });
        assertEquals("Эпика связанного с этой подзадачей не существует", thrown.getMessage());

        subtask.setId(INCORRECT_ID);
        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateSubTask(subtask);
        });
        assertEquals("Такой подзадачи не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateEpicTask(epic);
        });
        assertEquals("Такого эпика не существует", thrown.getMessage());
    }

    @Test
    @DisplayName("Получение списков всех задач")
    void shouldReturnEqualsTaskLists() {
        List<Task> list = new ArrayList<>();
        list.add(task);
        assertEquals(list, taskService.getAllSimpleTasks());

        list.clear();
        list.add(subtask);
        assertEquals(list, taskService.getAllSubTasks());

        list.clear();
        list.add(epic);
        assertEquals(list, taskService.getAllEpicTasks());
    }

    @Test
    @DisplayName("Получение списка подзадач эпика")
    void shouldReturnEqualsListWithSubtaskFromEpic() {
        List<Task> list = List.of(subtask);

        assertEquals(list, taskService.getAllSubTasksByEpic(epic));
    }

    @Test
    @DisplayName("Удаление задачи по айди")
    void shouldReturnEmptyListAfterDeletingSingleTask() {
        taskService.removeSimpleTaskById(task.getId());

        assertTrue(taskService.getAllSimpleTasks().isEmpty());
    }

    @Test
    @DisplayName("Удаление подзадачи по айди")
    void shouldReturnEmptyListAfterDeletingSingleSubtask() {
        taskService.removeSubTaskById(subtask.getId());

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllSubTasksByEpic(epic).isEmpty());
    }

    @Test
    @DisplayName("Удаление эпика по айди, имеющего связанные подзадачи")
    void shouldReturnTwoEmptyListsAfterDeletingSingleEpicWithSubtasks() {
        taskService.removeEpicTaskById(epic.getId());

        assertTrue(taskService.getAllEpicTasks().isEmpty());
        assertTrue(taskService.getAllSubTasks().isEmpty());
    }

    @Test
    @DisplayName("Удаление задач с некорректным id")
    void shouldBeThrownRuntimeExceptionWhenDeletingTasksWithIncorrectID() {
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeSimpleTaskById(INCORRECT_ID);
        });
        assertEquals("Задачи с айди {" + INCORRECT_ID + "} не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeSubTaskById(INCORRECT_ID);
        });
        assertEquals("Подзадачи с айди {" + INCORRECT_ID + "} не существует", thrown.getMessage());

        subtask.setEpicId(INCORRECT_ID);
        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeSubTaskById(subtask.getId());
        });
        assertEquals("Эпика связанного с этой подзадачей не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeEpicTaskById(INCORRECT_ID);
        });
        assertEquals("Эпика с айди {" + INCORRECT_ID + "} не существует", thrown.getMessage());
    }

    @Test
    @DisplayName("Удаление всех задач")
    void shouldReturnEmptyListAfterDeletingAllTasks() {
        taskService.removeAllSimpleTasks();

        assertTrue(taskService.getAllSimpleTasks().isEmpty());
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    void shouldReturnEmptyListsAfterDeletingAllSubtasks() {
        taskService.removeAllSubTasks();

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllSubTasksByEpic(epic).isEmpty());
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    void shouldReturnEmptyListAfterDeletingAllSubtasks() {
        taskService.removeAllEpicTasks();

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllEpicTasks().isEmpty());
    }

    @Test
    @DisplayName("Получение списка истории просмотров")
    void shouldReturnEqualTaskHistoryList() {
        List<Task> expectedList = new ArrayList<>() {{
            add(task);
            add(subtask);
            add(epic);
        }};

        taskService.getSimpleTaskById(task.getId());
        taskService.getSubTaskById(subtask.getId());
        taskService.getEpicTaskById(epic.getId());

        assertEquals(expectedList, taskService.getHistory());
    }
}