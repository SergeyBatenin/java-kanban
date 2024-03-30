package mapper;

import dto.SubtaskDto;
import model.SubTask;
import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubtaskMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static SubtaskDto subtaskToDto(SubTask subTask) {
        SubtaskDto subtaskDto = new SubtaskDto();
        subtaskDto.setId(subTask.getId());
        subtaskDto.setTaskType(subTask.getTaskType());
        subtaskDto.setName(subTask.getName());
        subtaskDto.setDescription(subTask.getDescription());
        subtaskDto.setStatus(subTask.getStatus());
        subtaskDto.setEpicId(subTask.getEpicId());
        subtaskDto.setStartTime(subTask.getStartTime());
        subtaskDto.setDuration(subTask.getDuration());
        return subtaskDto;
    }

    public static SubTask dtoToSubtask(SubtaskDto subtaskDto) {
        SubTask task = new SubTask();
        task.setId(subtaskDto.getId());
        task.setName(subtaskDto.getName());
        task.setDescription(subtaskDto.getDescription());
        task.setStatus(subtaskDto.getStatus());
        task.setEpicId(subtaskDto.getEpicId());
        task.setStartTime(subtaskDto.getStartTime());
        task.setDuration(subtaskDto.getDuration());
        return task;
    }

    public static String dtoToString(SubtaskDto subtaskDto) {
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s",
                subtaskDto.getId(),
                subtaskDto.getTaskType(),
                subtaskDto.getName(),
                subtaskDto.getStatus(),
                subtaskDto.getDescription(),
                subtaskDto.getStartTime() != null ? subtaskDto.getStartTime().format(DATE_TIME_FORMATTER) : "null",
                subtaskDto.getDuration().toMinutes(),
                subtaskDto.getEpicId());
    }

    public static SubtaskDto dtoFromData(String[] data) {
        SubtaskDto subtaskDto = new SubtaskDto();
        subtaskDto.setId(Long.parseLong(data[0]));
        subtaskDto.setTaskType(TaskType.valueOf(data[1]));
        subtaskDto.setName(data[2]);
        subtaskDto.setStatus(TaskStatus.valueOf(data[3]));
        subtaskDto.setDescription(data[4]);
        String startTime = data[5];
        if ("null".equals(startTime)) {
            subtaskDto.setStartTime(null);
        } else {
            subtaskDto.setStartTime(LocalDateTime.parse(startTime, DATE_TIME_FORMATTER));
        }
        subtaskDto.setDuration(Duration.ofMinutes(Long.parseLong(data[6])));
        subtaskDto.setEpicId(Long.parseLong(data[7]));
        return subtaskDto;
    }
}
