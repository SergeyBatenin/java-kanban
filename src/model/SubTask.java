package model;

public class SubTask extends Task {
    private long epicId;

    public SubTask() {
    }
    public SubTask(String name, String description, TaskStatus status, long epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId='" + epicId + '\'' +
                '}';
    }
}
