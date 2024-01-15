package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic() {
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
        this.subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
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
            for (SubTask subTask : subTasks) {
                result.append('\'').append(subTask.name).append("',");
            }
            result.setCharAt(result.length() - 1, '}');
        }
        result.append('}');
        return result.toString();
    }
}
