package dto;


import java.util.List;

public class EpicDto extends TaskDto {
    private List<Long> subTaskIds;

    public List<Long> getSubTaskIds() {
        return subTaskIds;
    }

    public void setSubTaskIds(List<Long> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }
}
