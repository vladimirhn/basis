package kpersistence.kfilters;

public enum SqlOperator {

    EQUALS(" = %s"), NOT_EQUALS(" != %s"),
    LESS(" < %s"), LESS_OR_EQUALS(" <= %s"), MORE(" > %s"), MORE_OR_EQUALS(" >= %s"),
    IN(" IN (%s)"), NOT_IN(" NOT IN (%s)"),
    LIKE(" LIKE %s"), _LIKE(" LIKE %s"), LIKE_(" LIKE %s"),
    IS_NULL(" IS NULL"), IS_NOT_NULL(" IS NOT NULL")

    ;

    private String usage;

    SqlOperator(String usage) {
        this.usage = usage;
    }

    public String getUsage() {
        return this.usage;
    }
}
