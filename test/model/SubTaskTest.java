package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    @Test
    @DisplayName("Подзадачи с одинаковым содержимым равны")
    void shouldBeEqualsSubTasks() {
        Epic epic = new Epic("epic", "description", TaskStatus.NEW);
        epic.setId(1);
        SubTask taskOne = new SubTask("name", "description", TaskStatus.NEW, 1);
        taskOne.setId(2);
        SubTask taskTwo = new SubTask("name", "description", TaskStatus.NEW, 1);
        taskTwo.setId(2);
        assertEquals(taskOne, taskTwo);
    }
}