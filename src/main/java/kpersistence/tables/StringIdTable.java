package kpersistence.tables;

import kpersistence.annotations.Column;
import kpersistence.annotations.Id;

public abstract class StringIdTable extends Table {

    @Id
    @Column(name = "ID", type = "id")
    private String id;

    public abstract void setDefaults();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
