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

class InMemoryTaskServiceTest {
    private TaskService taskService;

    @BeforeEach
    void initializeTaskService() {
        taskService = new InMemoryTaskService();
    }

    @Test
    @DisplayName("Создание новой задачи")
    void shouldCreateNewTask() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        final long taskId = taskService.createSimpleTask(task).getId();

        final Task savedTask = taskService.getSimpleTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание новой подзадачи")
    void shouldCreateNewSubTask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        final long taskId = taskService.createSubTask(task).getId();

        final SubTask savedTask = taskService.getSubTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Создание нового эпика")
    void shouldCreateNewEpic() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        long epicId = taskService.createEpicTask(epic).getId();

        final Epic savedTask = taskService.getEpicTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление задачи")
    void shouldBeEqualsTaskAfterUpdate() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        final long taskId = taskService.createSimpleTask(task).getId();

        task.setDescription("NEW description");
        taskService.updateSimpleTask(task);
        final Task savedTask = taskService.getSimpleTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление подзадачи")
    void shouldBeEqualsSubTaskAfterUpdate() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        final long taskId = taskService.createSubTask(task).getId();

        task.setDescription("NEW description");
        taskService.updateSubTask(task);

        final SubTask savedTask = taskService.getSubTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление эпика")
    void shouldBeEqualsEpicAfterUpdateField() {
        Epic task = new Epic("name", "description", TaskStatus.NEW);
        final long taskId = taskService.createEpicTask(task).getId();

        task.setDescription("NEW description");
        taskService.updateEpicTask(task);
        final Epic savedTask = taskService.getEpicTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика на \"В процессе\" после изменения подзадачи")
    void shouldBeEqualsEpicStatusIsProgressAfterUpdateSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        taskService.createSubTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskService.updateSubTask(task);

        final Epic savedTask = taskService.getEpicTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TaskStatus.IN_PROGRESS, savedTask.getStatus(), "Статусы не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика на \"Выполнена\" после изменения подзадачи")
    void shouldBeEqualsEpicStatusIsDoneAfterUpdateSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.NEW, epicId);
        taskService.createSubTask(task);
        task.setStatus(TaskStatus.DONE);
        taskService.updateSubTask(task);

        final Epic savedTask = taskService.getEpicTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TaskStatus.DONE, savedTask.getStatus(), "Статусы не совпадают.");
    }

    @Test
    @DisplayName("Обновление статуса эпика на \"Новый\" после удаления всех подзадач")
    void shouldBeEqualsEpicStatusIsNewAfterUpdateSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(epic).getId();

        SubTask task = new SubTask("name", "description", TaskStatus.IN_PROGRESS, epicId);
        taskService.createSubTask(task);

        final Epic savedTask = taskService.getEpicTaskById(epicId);
        taskService.removeAllSubTasks();

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статусы не совпадают.");
    }

    @Test
    @DisplayName("Создание подзадачи с некорректным родительским эпиком")
    void shouldBeThrownRuntimeExceptionWhenCreatingSubtaskWithIncorrectEpic() {
        SubTask incorrectSubtask = new SubTask("name", "description", TaskStatus.NEW, -1);
        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            taskService.createSubTask(incorrectSubtask);
        });
        assertEquals("Подзадача не может существовать без эпика", thrown.getMessage());
    }

    @Test
    @DisplayName("Обновление задач с некорректным id")
    void shouldBeThrownRuntimeExceptionWhenUpdatingTasksWithIncorrectID() {
        Task incorrectTask = new Task("name", "description", TaskStatus.NEW);
        taskService.createSimpleTask(incorrectTask);
        Epic incorrectEpic = new Epic("name", "description", TaskStatus.NEW);
        final long epicId = taskService.createEpicTask(incorrectEpic).getId();
        SubTask incorrectSubtask = new SubTask("name", "description", TaskStatus.NEW, epicId);
        taskService.createSubTask(incorrectSubtask);

        incorrectTask.setId(-1);
        incorrectEpic.setId(-1);
        incorrectSubtask.setEpicId(-1);

        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateSimpleTask(incorrectTask);
        });
        assertEquals("Такой задачи не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateSubTask(incorrectSubtask);
        });
        assertEquals("Эпика связанного с этой подзадачей не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateEpicTask(incorrectEpic);
        });
        assertEquals("Такого эпика не существует", thrown.getMessage());

        incorrectSubtask.setId(-1);
        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.updateSubTask(incorrectSubtask);
        });
        assertEquals("Такой подзадачи не существует", thrown.getMessage());
    }

    @Test
    @DisplayName("Получение списков всех задач")
    void shouldReturnEqualsTaskLists() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        task = taskService.createSimpleTask(task);
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        epic = taskService.createEpicTask(epic);
        SubTask subtask = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        subtask = taskService.createSubTask(subtask);

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
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        epic = taskService.createEpicTask(epic);
        SubTask subtask1 = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        subtask1 = taskService.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        subtask2 = taskService.createSubTask(subtask2);
        SubTask subtask3 = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        subtask3 = taskService.createSubTask(subtask3);

        List<Task> list = new ArrayList<>();
        list.add(subtask1);
        list.add(subtask2);
        list.add(subtask3);
        assertEquals(list, taskService.getAllSubTasksByEpic(epic));
    }

    @Test
    @DisplayName("Удаление задачи по айди")
    void shouldReturnEmptyListAfterDeletingSingleTask() {
        Task task = new Task("name", "description", TaskStatus.NEW);
        taskService.createSimpleTask(task);

        taskService.removeSimpleTaskById(task.getId());
        assertTrue(taskService.getAllSimpleTasks().isEmpty());

    }

    @Test
    @DisplayName("Удаление подзадачи по айди")
    void shouldReturnEmptyListAfterDeletingSingleSubtask() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        epic = taskService.createEpicTask(epic);
        SubTask subtask = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        taskService.createSubTask(subtask);

        taskService.removeSubTaskById(subtask.getId());

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllSubTasksByEpic(epic).isEmpty());
    }

    @Test
    @DisplayName("Удаление эпика по айди без связанных подзадач")
    void shouldReturnEmptyListAfterDeletingSingleEpicWithoutSubtasks() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        epic = taskService.createEpicTask(epic);

        taskService.removeEpicTaskById(epic.getId());

        assertTrue(taskService.getAllEpicTasks().isEmpty());
    }
    @Test
    @DisplayName("Удаление эпика по айди, имеющего связанные подзадачи")
    void shouldReturnTwoEmptyListsAfterDeletingSingleEpicWithSubtasks() {
        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        epic = taskService.createEpicTask(epic);
        SubTask subtask1 = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        taskService.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        taskService.createSubTask(subtask2);

        taskService.removeEpicTaskById(epic.getId());

        assertTrue(taskService.getAllEpicTasks().isEmpty());
        assertTrue(taskService.getAllSubTasks().isEmpty());
    }

    @Test
    @DisplayName("Удаление задач с некорректным id")
    void shouldBeThrownRuntimeExceptionWhenDeletingTasksWithIncorrectID() {

        long incorrectId = -1;

        Throwable thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeSimpleTaskById(incorrectId);
        });
        assertEquals("Задачи с айди {" + incorrectId + "} не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeSubTaskById(incorrectId);
        });
        assertEquals("Подзадачи с айди {" + incorrectId + "} не существует", thrown.getMessage());

        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeEpicTaskById(incorrectId);
        });
        assertEquals("Эпика с айди {" + incorrectId + "} не существует", thrown.getMessage());

        Epic epic = new Epic("name", "description", TaskStatus.NEW);
        epic = taskService.createEpicTask(epic);
        SubTask subtask1 = new SubTask("name", "description", TaskStatus.NEW, epic.getId());
        taskService.createSubTask(subtask1);
        subtask1.setEpicId(incorrectId);
        thrown = assertThrows(RuntimeException.class, () -> {
            taskService.removeSubTaskById(subtask1.getId());
        });
        assertEquals("Эпика связанного с этой подзадачей не существует", thrown.getMessage());
    }

    @Test
    @DisplayName("Удаление всех задач")
    void shouldReturnEmptyListAfterDeletingAllTasks() {
        Task task1 = new Task("name", "description", TaskStatus.NEW);
        Task task2 = new Task("name", "description", TaskStatus.NEW);
        Task task3 = new Task("name", "description", TaskStatus.NEW);
        taskService.createSimpleTask(task1);
        taskService.createSimpleTask(task2);
        taskService.createSimpleTask(task3);

        taskService.removeAllSimpleTasks();
        assertTrue(taskService.getAllSimpleTasks().isEmpty());
    }

    @Test
    @DisplayName("Удаление всех подзадач")
    void shouldReturnEmptyListsAfterDeletingAllSubtasks() {
        Epic epic1 = new Epic("name", "description", TaskStatus.NEW);
        epic1 = taskService.createEpicTask(epic1);
        Epic epic2 = new Epic("name", "description", TaskStatus.NEW);
        epic2 = taskService.createEpicTask(epic2);

        SubTask subtask1 = new SubTask("name", "description", TaskStatus.NEW, epic1.getId());
        taskService.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("name", "description", TaskStatus.NEW, epic2.getId());
        taskService.createSubTask(subtask2);
        SubTask subtask3 = new SubTask("name", "description", TaskStatus.NEW, epic2.getId());
        taskService.createSubTask(subtask3);

        taskService.removeAllSubTasks();

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllSubTasksByEpic(epic1).isEmpty());
        assertTrue(taskService.getAllSubTasksByEpic(epic2).isEmpty());
    }

    @Test
    @DisplayName("Удаление всех эпиков")
    void shouldReturnEmptyListAfterDeletingAllSubtasks() {
        Epic epic1 = new Epic("name", "description", TaskStatus.NEW);
        epic1 = taskService.createEpicTask(epic1);
        Epic epic2 = new Epic("name", "description", TaskStatus.NEW);
        epic2 = taskService.createEpicTask(epic2);

        SubTask subtask1 = new SubTask("name", "description", TaskStatus.NEW, epic1.getId());
        taskService.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("name", "description", TaskStatus.NEW, epic2.getId());
        taskService.createSubTask(subtask2);
        SubTask subtask3 = new SubTask("name", "description", TaskStatus.NEW, epic2.getId());
        taskService.createSubTask(subtask3);

        taskService.removeAllEpicTasks();

        assertTrue(taskService.getAllSubTasks().isEmpty());
        assertTrue(taskService.getAllEpicTasks().isEmpty());
    }

    @Test
    @DisplayName("Получение списка истории просмотров")
    void shouldReturnEqualTaskHistoryList() {
        Task task1 = new Task("name", "description", TaskStatus.NEW);
        Task task2 = new Task("name", "description", TaskStatus.NEW);
        Task task3 = new Task("name", "description", TaskStatus.NEW);
        taskService.createSimpleTask(task1);
        taskService.createSimpleTask(task2);
        taskService.createSimpleTask(task3);

        Epic epic1 = new Epic("name", "description", TaskStatus.NEW);
        taskService.createEpicTask(epic1);
        Epic epic2 = new Epic("name", "description", TaskStatus.NEW);
        taskService.createEpicTask(epic2);

        SubTask subtask1 = new SubTask("name", "description", TaskStatus.NEW, epic1.getId());
        taskService.createSubTask(subtask1);
        SubTask subtask2 = new SubTask("name", "description", TaskStatus.NEW, epic2.getId());
        taskService.createSubTask(subtask2);
        SubTask subtask3 = new SubTask("name", "description", TaskStatus.NEW, epic2.getId());
        taskService.createSubTask(subtask3);

        List<Task> expectedList = new ArrayList<>() {{
            add(task1);
            add(task2);
            add(task3);
            add(epic1);
            add(epic2);
            add(subtask1);
            add(subtask2);
            add(subtask3);
        }};

        taskService.getSimpleTaskById(task1.getId());
        taskService.getSimpleTaskById(task2.getId());
        taskService.getSimpleTaskById(task3.getId());
        taskService.getEpicTaskById(epic1.getId());
        taskService.getEpicTaskById(epic2.getId());
        taskService.getSubTaskById(subtask1.getId());
        taskService.getSubTaskById(subtask2.getId());
        taskService.getSubTaskById(subtask3.getId());

        assertEquals(expectedList, taskService.getHistory());
    }
}