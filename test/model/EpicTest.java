package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    @DisplayName("Эпики с одинаковым содержимым, без подзадач равны")
    void shouldBeEqualsEpicsWithoutSubtasks() {
        Epic epicOne = new Epic("epic", "description", TaskStatus.NEW, null, Duration.ZERO);
        epicOne.setId(1);
        Epic epicTwo = new Epic("epic", "description", TaskStatus.NEW, null, Duration.ZERO);
        epicTwo.setId(1);
        assertEquals(epicOne, epicTwo);
    }

    @Test
    @DisplayName("Эпики с одинаковым содержимым, без подзадач равны")
    void shouldBeEqualsEpicsWithSubtask() {
        Epic epicOne = new Epic("epic", "description", TaskStatus.NEW, null, Duration.ZERO);
        epicOne.setId(1);
        Epic epicTwo = new Epic("epic", "description", TaskStatus.NEW, null, Duration.ZERO);
        epicTwo.setId(1);
        epicOne.getSubTaskIds().add(2L);
        epicTwo.getSubTaskIds().add(2L);
        assertEquals(epicOne, epicTwo);
    }
}