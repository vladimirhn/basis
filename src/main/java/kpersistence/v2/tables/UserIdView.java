package kpersistence.v2.tables;

import kpersistence.v1.mapping.annotations.CurrentUserId;

public abstract class UserIdView extends Table {

    @CurrentUserId
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
