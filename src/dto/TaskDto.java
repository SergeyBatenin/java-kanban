package dto;

import model.Task;
import model.TaskStatus;
import model.TaskType;

public class TaskDto {
    protected long id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType taskType;

    public TaskDto() {
    }

    public TaskDto(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.taskType = task.getTaskType();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", id, taskType, name, status, description);
    }
}
