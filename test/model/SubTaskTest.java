package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    @DisplayName("Подзадачи с одинаковым содержимым равны")
    void shouldBeEqualsSubTasks() {
        Epic epic = new Epic(1, "epic", "description", TaskStatus.NEW, null, Duration.ZERO);
        SubTask taskOne = new SubTask(
                2,
                "name",
                "description",
                TaskStatus.NEW,
                1,
                null,
                Duration.ofMinutes(15));
        SubTask taskTwo = new SubTask(
                2,
                "name",
                "description",
                TaskStatus.NEW,
                1,
                null,
                Duration.ofMinutes(15));
        assertEquals(taskOne, taskTwo);

        taskTwo.setStartTime(LocalDateTime.of(2024, 3, 30, 17, 30));
        SubTask taskThree = new SubTask(
                2,
                "name",
                "description",
                TaskStatus.NEW,
                1,
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        assertEquals(taskTwo, taskThree);
    }
}