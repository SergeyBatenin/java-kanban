package mapper;

import dto.EpicDto;
import model.Epic;
import model.TaskStatus;
import model.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EpicMapper {
    public static EpicDto epicToDto(Epic epic) {
        EpicDto epicDto = new EpicDto();
        epicDto.setId(epic.getId());
        epicDto.setTaskType(epic.getTaskType());
        epicDto.setName(epic.getName());
        epicDto.setDescription(epic.getDescription());
        epicDto.setStatus(epic.getStatus());
        epicDto.setSubTaskIds(epic.getSubTaskIds());
        return epicDto;
    }

    public static Epic dtoToEpic(EpicDto epicDto) {
        Epic task = new Epic();
        task.setId(epicDto.getId());
        task.setName(epicDto.getName());
        task.setDescription(epicDto.getDescription());
        task.setStatus(epicDto.getStatus());
        task.setSubTaskIds(epicDto.getSubTaskIds());
        return task;
    }

    public static String dtoToString(EpicDto epicDto) {
        String formattedSubtaskIDs = epicDto.getSubTaskIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return String.format("%d,%s,%s,%s,%s,%s",
                epicDto.getId(),
                epicDto.getTaskType(),
                epicDto.getName(),
                epicDto.getStatus(),
                epicDto.getDescription(),
                formattedSubtaskIDs);
    }

    public static EpicDto dtoFromData(String[] data) {
        EpicDto epicDto = new EpicDto();
        epicDto.setId(Long.parseLong(data[0]));
        epicDto.setTaskType(TaskType.valueOf(data[1]));
        epicDto.setName(data[2]);
        epicDto.setStatus(TaskStatus.valueOf(data[3]));
        epicDto.setDescription(data[4]);

        if (data.length > 5) {
            final List<Long> subtasksIds = new ArrayList<>();
            for (int i = 5; i < data.length; i++) {
                subtasksIds.add(Long.parseLong(data[i]));
            }
            epicDto.setSubTaskIds(subtasksIds);
        } else {
            epicDto.setSubTaskIds(new ArrayList<>());
        }
        return epicDto;
    }
}
