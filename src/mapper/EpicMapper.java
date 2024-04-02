package mapper;

import dto.EpicDto;
import model.Epic;
import model.TaskStatus;
import model.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EpicMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static EpicDto epicToDto(Epic epic) {
        EpicDto epicDto = new EpicDto();
        epicDto.setId(epic.getId());
        epicDto.setTaskType(epic.getTaskType());
        epicDto.setName(epic.getName());
        epicDto.setDescription(epic.getDescription());
        epicDto.setStatus(epic.getStatus());
        epicDto.setSubTaskIds(epic.getSubTaskIds());
        epicDto.setStartTime(epic.getStartTime());
        epicDto.setDuration(epic.getDuration());
        return epicDto;
    }

    public static Epic dtoToEpic(EpicDto epicDto) {
        Epic task = new Epic();
        task.setId(epicDto.getId());
        task.setName(epicDto.getName());
        task.setDescription(epicDto.getDescription());
        task.setStatus(epicDto.getStatus());
        task.setSubTaskIds(epicDto.getSubTaskIds());

        LocalDateTime startTime = epicDto.getStartTime();
        Duration duration = epicDto.getDuration();
        task.setStartTime(startTime);
        task.setDuration(duration);
        task.setEndTime(
                startTime != null ? startTime.plus(duration) : null
        );
        return task;
    }

    public static String dtoToString(EpicDto epicDto) {
        String formattedSubtaskIDs = epicDto.getSubTaskIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return String.format("%d,%s,%s,%s,%s,%s,%d,%s",
                epicDto.getId(),
                epicDto.getTaskType(),
                epicDto.getName(),
                epicDto.getStatus(),
                epicDto.getDescription(),
                epicDto.getStartTime() != null ? epicDto.getStartTime().format(DATE_TIME_FORMATTER) : "null",
                epicDto.getDuration().toMinutes(),
                formattedSubtaskIDs);
    }

    public static EpicDto dtoFromData(String[] data) {
        EpicDto epicDto = new EpicDto();
        epicDto.setId(Long.parseLong(data[0]));
        epicDto.setTaskType(TaskType.valueOf(data[1]));
        epicDto.setName(data[2]);
        epicDto.setStatus(TaskStatus.valueOf(data[3]));
        epicDto.setDescription(data[4]);
        String startTime = data[5];
        if ("null".equals(startTime)) {
            epicDto.setStartTime(null);
        } else {
            epicDto.setStartTime(LocalDateTime.parse(startTime, DATE_TIME_FORMATTER));
        }
        epicDto.setDuration(Duration.ofMinutes(Long.parseLong(data[6])));

        if (data.length > 7) {
            final List<Long> subtasksIds = new ArrayList<>();
            for (int i = 7; i < data.length; i++) {
                subtasksIds.add(Long.parseLong(data[i]));
            }
            epicDto.setSubTaskIds(subtasksIds);
        } else {
            epicDto.setSubTaskIds(new ArrayList<>());
        }
        return epicDto;
    }
}
