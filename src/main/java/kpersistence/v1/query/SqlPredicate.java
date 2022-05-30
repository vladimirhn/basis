package kpersistence.v1.query;

import kpersistence.v1.kfilters.SqlOperator;

public class SqlPredicate {

    String column;
    SqlOperator operator;
    Object value;

    public SqlPredicate(String column, SqlOperator operator, Object value) {
        this.column = column;
        this.operator = operator;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public SqlOperator getOperator() {
        return operator;
    }

    public void setOperator(SqlOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
