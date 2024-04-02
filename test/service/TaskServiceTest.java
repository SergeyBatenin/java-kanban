package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskServiceTest<T extends TaskService> {
    protected static final long INCORRECT_ID = -1L;
    protected T taskService;
    protected Task task;
    protected SubTask subtask;
    protected Epic epic;

    abstract void initializeTaskService();

    void initializeTasks() {
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
    @DisplayName("Создание новой задачи, пересекающейся по времени")
    void shouldBeThrownRuntimeExceptionWhenCreatingTaskWithTimeOverlap() {
        Task taskWithTimeOverlap = new Task(
                "name",
                "description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        List<Task> expectedPriorityList = List.of(subtask, task);

        assertEquals(expectedPriorityList, taskService.getPrioritizedTasks(),
                "Список отсортированных задач не совпадает");
        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.createSimpleTask(taskWithTimeOverlap));
        assertEquals("Задача имеет пересечение времени ввыполнения", thrown.getMessage());
        assertEquals(expectedPriorityList, taskService.getPrioritizedTasks(),
                "Список отсортированных задач не совпадает");
    }

    @Test
    @DisplayName("Создание новой подзадачи")
    void shouldCreateNewSubTask() {
        final SubTask savedTask = taskService.getSubTaskById(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание новой подзадачи, пересекающейся по времени")
    void shouldBeThrownRuntimeExceptionWhenCreatingSubtaskWithTimeOverlap() {
        SubTask taskWithTimeOverlap = new SubTask(
                "name",
                "description",
                TaskStatus.NEW,
                epic.getId(),
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        List<Task> expectedPriorityList = List.of(subtask, task);

        assertEquals(expectedPriorityList, taskService.getPrioritizedTasks(),
                "Список отсортированных задач не совпадает");
        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.createSubTask(taskWithTimeOverlap));
        assertEquals("Задача имеет пересечение времени ввыполнения", thrown.getMessage());
        assertEquals(expectedPriorityList, taskService.getPrioritizedTasks(),
                "Список отсортированных задач не совпадает");
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
        task = new Task(
                task.getId(),
                "name",
                "NEW description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        taskService.updateSimpleTask(task);
        final Task savedTask = taskService.getSimpleTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление задачи, пересекающейся по времени")
    void shouldBeThrownRuntimeExceptionWhenUpdatingTaskWithTimeOverlap() {
        Task taskWithTimeOverlap = new Task(
                task.getId(),
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));

        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.updateSimpleTask(taskWithTimeOverlap));
        assertEquals("Обновление данных невозможно! Задача имеет пересечение времени ввыполнения",
                thrown.getMessage());
    }

    @Test
    @DisplayName("Обновление подзадачи")
    void shouldBeEqualsSubTaskAfterUpdate() {
        subtask = new SubTask(
                subtask.getId(),
                "name",
                "NEW description",
                TaskStatus.NEW,
                epic.getId(),
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));
        taskService.updateSubTask(subtask);
        final SubTask savedTask = taskService.getSubTaskById(subtask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление подзадачи, пересекающейся по времени")
    void shouldBeThrownRuntimeExceptionWhenUpdatingSubtaskWithTimeOverlap() {
        SubTask taskWithTimeOverlap = new SubTask(
                subtask.getId(),
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                epic.getId(),
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));

        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.updateSubTask(taskWithTimeOverlap));
        assertEquals("Обновление данных невозможно! Задача имеет пересечение времени ввыполнения",
                thrown.getMessage());
    }

    @Test
    @DisplayName("Обновление эпика")
    void shouldBeEqualsEpicAfterUpdateField() {
        Epic epicForUpdate = new Epic(
                epic.getId(),
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                null,
                Duration.ZERO);
        taskService.updateEpicTask(epicForUpdate);
        final Epic savedTask = taskService.getEpicTaskById(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика при изменении подзадач")
    void shouldBeEqualsEpicStatusIsProgressAfterUpdateSubtask() {
        final Epic newEpic = taskService.getEpicTaskById(epic.getId());
        assertNotNull(newEpic, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, newEpic.getStatus(), "Статусы не совпадают.");

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
        SubTask subTask = new SubTask(
                "name",
                "NEW description",
                TaskStatus.NEW,
                INCORRECT_ID,
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));

        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.createSubTask(subTask));
        assertEquals("Подзадача не может существовать без эпика", thrown.getMessage());
    }

    @Test
    @DisplayName("Обновление задач с некорректным id")
    void shouldBeThrownRuntimeExceptionWhenUpdatingTasksWithIncorrectID() {
        task = new Task(
                INCORRECT_ID,
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));
        subtask = new SubTask(
                subtask.getId(),
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                INCORRECT_ID,
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));
        epic = new Epic(
                INCORRECT_ID,
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));

        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.updateSimpleTask(task));
        assertEquals("Такой задачи не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");

        thrown = assertThrows(RuntimeException.class, () -> taskService.updateSubTask(subtask));
        assertEquals("Эпика связанного с этой подзадачей не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");

        subtask = new SubTask(
                INCORRECT_ID,
                "NEW name",
                "NEW description",
                TaskStatus.NEW,
                subtask.getEpicId(),
                LocalDateTime.of(2024, 3, 29, 17, 30),
                Duration.ofMinutes(15));
        thrown = assertThrows(RuntimeException.class, () -> taskService.updateSubTask(subtask));
        assertEquals("Такой подзадачи не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");

        thrown = assertThrows(RuntimeException.class, () -> taskService.updateEpicTask(epic));
        assertEquals("Такого эпика не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");
    }

    @Test
    @DisplayName("Получение списков всех задач")
    void shouldReturnEqualsTaskLists() {
        List<Task> list = new ArrayList<>();
        list.add(task);
        assertEquals(list, taskService.getAllSimpleTasks(), "Списки задач не совпадают");

        list.clear();
        list.add(subtask);
        assertEquals(list, taskService.getAllSubTasks(), "Списки задач не совпадают");

        list.clear();
        list.add(epic);
        assertEquals(list, taskService.getAllEpicTasks(), "Списки задач не совпадают");
    }

    @Test
    @DisplayName("Получение списка подзадач эпика")
    void shouldReturnEqualsListWithSubtaskFromEpic() {
        List<Task> list = List.of(subtask);

        assertEquals(list, taskService.getAllSubTasksByEpic(epic), "Списки задач не совпадают");
    }

    @Test
    @DisplayName("Удаление задачи по айди")
    void shouldReturnEmptyListAfterDeletingSingleTask() {
        taskService.removeSimpleTaskById(task.getId());

        assertTrue(taskService.getAllSimpleTasks().isEmpty(), "Задача не удалилась");
    }

    @Test
    @DisplayName("Удаление подзадачи по айди")
    void shouldReturnEmptyListAfterDeletingSingleSubtask() {
        taskService.removeSubTaskById(subtask.getId());

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllSubTasksByEpic(epic).isEmpty(), "Задача не удалилась");
    }

    @Test
    @DisplayName("Удаление эпика по айди, имеющего связанные подзадачи")
    void shouldReturnTwoEmptyListsAfterDeletingSingleEpicWithSubtasks() {
        taskService.removeEpicTaskById(epic.getId());

        assertTrue(taskService.getAllSubTasks().isEmpty(), "Подзадачи эпика не удалились");
        assertTrue(taskService.getAllEpicTasks().isEmpty(), "Эпик не удалился");
    }

    @Test
    @DisplayName("Удаление задач с некорректным id")
    void shouldBeThrownRuntimeExceptionWhenDeletingTasksWithIncorrectID() {
        Throwable thrown = assertThrows(RuntimeException.class, () -> taskService.removeSimpleTaskById(INCORRECT_ID));
        assertEquals("Задачи с айди {" + INCORRECT_ID + "} не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");

        thrown = assertThrows(RuntimeException.class, () -> taskService.removeSubTaskById(INCORRECT_ID));
        assertEquals("Подзадачи с айди {" + INCORRECT_ID + "} не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");

        subtask.setEpicId(INCORRECT_ID);
        thrown = assertThrows(RuntimeException.class, () -> taskService.removeSubTaskById(subtask.getId()));
        assertEquals("Эпика связанного с этой подзадачей не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");

        thrown = assertThrows(RuntimeException.class, () -> taskService.removeEpicTaskById(INCORRECT_ID));
        assertEquals("Эпика с айди {" + INCORRECT_ID + "} не существует", thrown.getMessage(),
                "Сообщение об ошибке не совпадают");
    }

    @Test
    @DisplayName("Удаление всех задач")
    void shouldReturnEmptyListAfterDeletingAllTasks() {
        taskService.removeAllSimpleTasks();

        assertTrue(taskService.getAllSimpleTasks().isEmpty(), "Не все задачи удалились");
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    void shouldReturnEmptyListsAfterDeletingAllSubtasks() {
        taskService.removeAllSubTasks();

        assertTrue(taskService.getAllSubTasks().isEmpty(), "Не все задачи удалились");
        assertTrue(taskService.getAllSubTasksByEpic(epic).isEmpty(), "Не все задачи удалились");
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    void shouldReturnEmptyListAfterDeletingAllSubtasks() {
        taskService.removeAllEpicTasks();

        assertTrue(taskService.getAllSubTasks().isEmpty(), "Не все задачи удалились");
        assertTrue(taskService.getAllEpicTasks().isEmpty(), "Не все задачи удалились");
    }

    @Test
    @DisplayName("Получение списка истории просмотров")
    void shouldReturnEqualTaskHistoryList() {
        List<Task> expectedList = new ArrayList<>();
        expectedList.add(epic);
        expectedList.add(task);
        expectedList.add(subtask);

        taskService.getEpicTaskById(epic.getId());
        taskService.getSimpleTaskById(task.getId());
        taskService.getSubTaskById(subtask.getId());

        assertEquals(expectedList, taskService.getHistory(), "История задач не совпадает");

        taskService.removeSimpleTaskById(task.getId());
        expectedList.remove(1);
        assertEquals(expectedList, taskService.getHistory(), "История задач не совпадает");
    }

    @Test
    @DisplayName("Получение отсортированного по времени начала списка задач")
    void shouldReturnEqualsPrioritizedTasksList() {
        List<Task> expectedList = List.of(subtask, task);

        assertEquals(expectedList, taskService.getPrioritizedTasks(), "История задач не совпадает");
    }
}