package dto;

import model.SubTask;

public class SubtaskDto extends TaskDto {
    private long epicId;

    public SubtaskDto(SubTask subTask) {
        this.id = subTask.getId();
        this.name = subTask.getName();
        this.description = subTask.getDescription();
        this.status = subTask.getStatus();
        this.taskType = subTask.getTaskType();
        this.epicId = subTask.getEpicId();
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", id, taskType, name, status, description, epicId);
    }
}
