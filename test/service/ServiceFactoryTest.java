package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceFactoryTest {

    @Test
    @DisplayName("Должен вернуть класс, реализующий TaskService")
    void shouldReturnTrueIfItReturnsTaskService() {
        assertNotNull(ServiceFactory.getDefaultTaskService());
        assertInstanceOf(TaskService.class, ServiceFactory.getDefaultTaskService());
    }

    @Test
    @DisplayName("Должен вернуть класс, реализующий HistoryService")
    void shouldReturnTrueIfItReturnsHistoryService() {
        assertNotNull(ServiceFactory.getDefaultTaskService());
        assertInstanceOf(HistoryService.class, ServiceFactory.getDefaultHistoryService());
    }

}