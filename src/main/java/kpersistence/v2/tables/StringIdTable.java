package kpersistence.v2.tables;

import kpersistence.v2.annotations.Column;
import kpersistence.v2.annotations.Id;

public abstract class StringIdTable extends Table {

    @Id
    @Column(name = "ID")
    private String id;

    public abstract void setDefaults();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
