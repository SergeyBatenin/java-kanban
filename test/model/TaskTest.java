package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    @DisplayName("Задачи с одинаковым содержимым равны")
    void shouldBeEqualsTasks() {
        Task taskOne = new Task(
                1,
                "name",
                "description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        Task taskTwo = new Task(
                1,
                "name",
                "description",
                TaskStatus.NEW,
                LocalDateTime.of(2024, 3, 30, 17, 30),
                Duration.ofMinutes(15));
        assertEquals(taskOne, taskTwo);
    }
}