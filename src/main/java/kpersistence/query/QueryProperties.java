package kpersistence.query;

import kcollections.CollectionFactory;
import kcollections.KList;
import kpersistence.mapping.annotations.Direction;


public class QueryProperties<T> {

    private Class<T> clazz;
    private KList<SqlPredicate> filters;
    private String orderBy;
    private Direction orderDirection;

    public static <T>QueryProperties<T> createDefault(Class<T> clazz) {
        return new QueryProperties<>(clazz, CollectionFactory.makeList(), null, null);
    }

    public QueryProperties() {}

    public QueryProperties(Class<T> clazz, KList<SqlPredicate> filters, String orderBy, Direction orderDirection) {
        this.clazz = clazz;
        this.filters = filters;
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public KList<SqlPredicate> getFilters() {
        return filters;
    }

    public void setFilters(KList<SqlPredicate> filters) {
        this.filters = filters;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Direction getOrderDirection() {
        return orderDirection;
    }

    public void setOrderDirection(Direction orderDirection) {
        this.orderDirection = orderDirection;
    }
}
