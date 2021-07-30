package tm;

public class TodoTM {
    private String id;
    private String description;
    private String userId;

    public TodoTM() {
    }

    public TodoTM(String id, String description, String userId) {
        this.id = id;
        this.description = description;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return  description ;
    }
}
