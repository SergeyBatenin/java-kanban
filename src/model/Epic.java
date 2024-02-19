package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Long> subTaskIds;

    public Epic() {
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subTaskIds = new ArrayList<>();
    }

    public List<Long> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Long> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Epic epic = (Epic) o;

        return subTaskIds.equals(epic.subTaskIds);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + subTaskIds.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
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
