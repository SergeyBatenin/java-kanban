package model;

public class SimpleTask extends Task {
    public SimpleTask() {
    }
    public SimpleTask(String name, String description) {
        super(name, description);
    }

    @Override
    public String toString() {
        return "SimpleTask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
