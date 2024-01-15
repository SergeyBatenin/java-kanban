package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Long> subTasks;

    public Epic() {
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subTasks = new ArrayList<>();
    }

    public List<Long> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<Long> subTasks) {
        this.subTasks = subTasks;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subTasks={");

        if (subTasks.isEmpty()) {
            result.append("Empty}");
        } else {
            for (long id : subTasks) {
                result.append('\'').append(id).append("',");
            }
            result.setCharAt(result.length() - 1, '}');
        }
        result.append('}');
        return result.toString();
    }
}
