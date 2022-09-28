package kpersistence.kfilters;

import java.util.function.BiConsumer;

public class FieldRef<T,V> {
    private BiConsumer<T,V> setter;

    public FieldRef(BiConsumer<T,V> setter) {
        this.setter = setter;
    }

    public BiConsumer<T,V> getSetter() {
        return setter;
    }
}
