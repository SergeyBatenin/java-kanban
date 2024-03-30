package mapper;

import dto.TaskDto;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static TaskDto taskToDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTaskType(task.getTaskType());
        taskDto.setName(task.getName());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        taskDto.setStartTime(task.getStartTime());
        taskDto.setDuration(task.getDuration());
        return taskDto;
    }

    public static Task dtoToTask(TaskDto taskDto) {
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setStartTime(taskDto.getStartTime());
        task.setDuration(taskDto.getDuration());
        return task;
    }

    public static String dtoToString(TaskDto taskDto) {
        return String.format("%d,%s,%s,%s,%s,%s,%d",
                taskDto.getId(),
                taskDto.getTaskType(),
                taskDto.getName(),
                taskDto.getStatus(),
                taskDto.getDescription(),
                taskDto.getStartTime() != null ? taskDto.getStartTime().format(DATE_TIME_FORMATTER) : "null",
                taskDto.getDuration().toMinutes());
    }

    public static TaskDto dtoFromData(String[] data) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(Long.parseLong(data[0]));
        taskDto.setTaskType(TaskType.valueOf(data[1]));
        taskDto.setName(data[2]);
        taskDto.setStatus(TaskStatus.valueOf(data[3]));
        taskDto.setDescription(data[4]);
        String startTime = data[5];
        if ("null".equals(startTime)) {
            taskDto.setStartTime(null);
        } else {
            taskDto.setStartTime(LocalDateTime.parse(startTime, DATE_TIME_FORMATTER));
        }
        taskDto.setDuration(Duration.ofMinutes(Long.parseLong(data[6])));
        return taskDto;
    }
}
