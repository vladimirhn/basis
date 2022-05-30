package kpersistence.v2.modelsMaster;

import java.sql.ResultSet;

@FunctionalInterface
public interface SqlDataSetter {
    void setData(ResultSet rs, Object mainDTO);
}
