package kpersistence.kfilters;

import java.util.ArrayList;
import java.util.List;

public class RamMap {
    private List<Object> keys = new ArrayList<>();
    private List<String> values = new ArrayList<>();

    public void put(Object key, String value) {
        keys.add(key);
        values.add(value);
    }

    public String get(Object key) {
        int i = 0;

        for (; i < keys.size(); i++) {
            if (keys.get(i) == key) {
                break;
            }
        }

        if (i == keys.size()) {
            return null;
        }

        return values.get(i);
    }
}
