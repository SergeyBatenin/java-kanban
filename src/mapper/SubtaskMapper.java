package mapper;

import dto.SubtaskDto;
import model.SubTask;
import model.TaskStatus;
import model.TaskType;

public class SubtaskMapper {
    public static SubtaskDto subtaskToDto(SubTask subTask) {
        SubtaskDto subtaskDto = new SubtaskDto();
        subtaskDto.setId(subTask.getId());
        subtaskDto.setTaskType(subTask.getTaskType());
        subtaskDto.setName(subTask.getName());
        subtaskDto.setDescription(subTask.getDescription());
        subtaskDto.setStatus(subTask.getStatus());
        subtaskDto.setEpicId(subTask.getEpicId());
        return subtaskDto;
    }

    public static SubTask dtoToSubtask(SubtaskDto subtaskDto) {
        SubTask task = new SubTask();
        task.setId(subtaskDto.getId());
        task.setName(subtaskDto.getName());
        task.setDescription(subtaskDto.getDescription());
        task.setStatus(subtaskDto.getStatus());
        task.setEpicId(subtaskDto.getEpicId());
        return task;
    }

    public static String dtoToString(SubtaskDto subtaskDto) {
        return String.format("%d,%s,%s,%s,%s,%d",
                subtaskDto.getId(),
                subtaskDto.getTaskType(),
                subtaskDto.getName(),
                subtaskDto.getStatus(),
                subtaskDto.getDescription(),
                subtaskDto.getEpicId());
    }

    public static SubtaskDto dtoFromData(String[] data) {
        SubtaskDto subtaskDto = new SubtaskDto();
        subtaskDto.setId(Long.parseLong(data[0]));
        subtaskDto.setTaskType(TaskType.valueOf(data[1]));
        subtaskDto.setName(data[2]);
        subtaskDto.setStatus(TaskStatus.valueOf(data[3]));
        subtaskDto.setDescription(data[4]);
        subtaskDto.setEpicId(Long.parseLong(data[5]));
        return subtaskDto;
    }
}
