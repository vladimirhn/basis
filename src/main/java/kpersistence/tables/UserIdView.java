package kpersistence.tables;

public abstract class UserIdView extends Table {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
