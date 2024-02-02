package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    @DisplayName("Задачи с одинаковым содержимым равны")
    void shouldBeEqualsTasks() {
        Task taskOne = new Task("name", "description", TaskStatus.NEW);
        taskOne.setId(1);
        Task taskTwo = new Task("name", "description", TaskStatus.NEW);
        taskTwo.setId(1);
        assertEquals(taskOne, taskTwo);
    }
}