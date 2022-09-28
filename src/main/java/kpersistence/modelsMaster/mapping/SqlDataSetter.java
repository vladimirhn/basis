package kpersistence.modelsMaster.mapping;

import java.sql.ResultSet;

@FunctionalInterface
public interface SqlDataSetter {
    void setData(ResultSet rs, Object mainDTO);
}
