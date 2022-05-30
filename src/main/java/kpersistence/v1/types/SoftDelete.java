package kpersistence.v1.types;

public interface SoftDelete {

    Boolean getDeleted();
    void setDeleted(Boolean isDeleted);
}
