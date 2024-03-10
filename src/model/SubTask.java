package model;

public class SubTask extends Task {
    private long epicId;

    public SubTask() {
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(int id, String name, String description, TaskStatus status, long epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(String name, String description, TaskStatus status, long epicId) {
        super(name, description, status);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
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
                '}';
    }
}
