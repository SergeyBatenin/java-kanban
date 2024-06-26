package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private long epicId;

    public SubTask() {
    }

    public SubTask(long id, String name, String description, TaskStatus status, long epicId, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, long epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SubTask subTask = (SubTask) o;

        return epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (epicId ^ (epicId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                ", startTime=" + (startTime != null ? startTime.format(DATE_TIME_FORMATTER) : null) +
                ", duration=" + duration.toHours() + ":" + duration.toMinutesPart() +
                '}';
    }
}
