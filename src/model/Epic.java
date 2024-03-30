package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Long> subTaskIds;
    private LocalDateTime endTime;

    public Epic() {
        this.subTaskIds = new ArrayList<>();
        this.duration = Duration.ZERO;
        this.endTime = super.getEndTime();
    }

    public Epic(long id, String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.subTaskIds = new ArrayList<>();
        this.endTime = super.getEndTime();
    }

    public Epic(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.subTaskIds = new ArrayList<>();
        this.endTime = super.getEndTime();
    }

    public List<Long> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Long> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        if (!subTaskIds.equals(epic.subTaskIds)) return false;
        if (endTime == null && epic.endTime == null) return true;
        if (endTime != null && epic.endTime != null) {
            return endTime.equals(epic.endTime);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subTaskIds.hashCode();
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + (startTime != null ? startTime.format(DATE_TIME_FORMATTER) : null) +
                ", duration=" + duration.toHours() + ":" + duration.toMinutesPart() +
                ", subTasks={");

        if (subTaskIds.isEmpty()) {
            result.append("Empty}");
        } else {
            for (long id : subTaskIds) {
                result.append(id).append(",");
            }
            result.setCharAt(result.length() - 1, '}');
        }
        result.append('}');
        return result.toString();
    }
}
