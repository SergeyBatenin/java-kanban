package mapper;

import dto.TaskDto;
import model.Task;
import model.TaskStatus;
import model.TaskType;

public class TaskMapper {

    public static TaskDto taskToDto(Task task) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(task.getId());
        taskDto.setTaskType(task.getTaskType());
        taskDto.setName(task.getName());
        taskDto.setDescription(task.getDescription());
        taskDto.setStatus(task.getStatus());
        return taskDto;
    }

    public static Task dtoToTask(TaskDto taskDto) {
        Task task = new Task();
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        return task;
    }

    public static String dtoToString(TaskDto taskDto) {
        return String.format("%d,%s,%s,%s,%s",
                taskDto.getId(), taskDto.getTaskType(), taskDto.getName(), taskDto.getStatus(), taskDto.getDescription());
    }

    public static TaskDto dtoFromData(String[] data) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(Long.parseLong(data[0]));
        taskDto.setTaskType(TaskType.valueOf(data[1]));
        taskDto.setName(data[2]);
        taskDto.setStatus(TaskStatus.valueOf(data[3]));
        taskDto.setDescription(data[4]);
        return taskDto;
    }
}
