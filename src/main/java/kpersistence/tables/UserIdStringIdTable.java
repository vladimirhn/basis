package kpersistence.tables;

public abstract class UserIdStringIdTable extends StringIdTable {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
