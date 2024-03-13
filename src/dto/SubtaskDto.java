package dto;


public class SubtaskDto extends TaskDto {
    private long epicId;

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }
}
