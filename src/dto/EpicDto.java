package dto;

import model.Epic;

import java.util.List;
import java.util.stream.Collectors;

public class EpicDto extends TaskDto {
    private List<Long> subTaskIds;

    public EpicDto(Epic epic) {
        this.id = epic.getId();
        this.name = epic.getName();
        this.description = epic.getDescription();
        this.status = epic.getStatus();
        this.taskType = epic.getTaskType();
        this.subTaskIds = epic.getSubTaskIds();
    }

    public List<Long> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Long> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public String toString() {
        String formattedSubtaskIDs = subTaskIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        return String.format("%d,%s,%s,%s,%s,%s", id, taskType, name, status, description, formattedSubtaskIDs);
    }
}
