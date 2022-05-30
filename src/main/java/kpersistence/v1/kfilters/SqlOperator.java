package kpersistence.v1.kfilters;

public enum SqlOperator {

    EQUALS(" = %s", " = ? "), NOT_EQUALS(" != %s", " != ? "),
    LESS(" < %s", " < ?"), LESS_OR_EQUALS(" <= %s", " <= ?"),
    MORE(" > %s", " > ?"), MORE_OR_EQUALS(" >= %s", " >= ?"),
    IN(" IN (%s)", " IN (?) "), NOT_IN(" NOT IN (%s)", " NOT IN (?) "),
    LIKE(" LIKE %s", " LIKE ? "), _LIKE(" LIKE %s", " LIKE ? "), LIKE_(" LIKE %s", " LIKE ?"),
    IS_NULL(" IS NULL", "IS NULL "), IS_NOT_NULL(" IS NOT NULL", " IS NOT NULL ")

    ;

    private final String usage;
    private final String unnamedUsage;

    SqlOperator(String usage, String unnamedUsage) {
        this.usage = usage;
        this.unnamedUsage = unnamedUsage;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getUnnamedUsage() {
        return unnamedUsage;
    }
}
