package kmodels.abstracts;

import java.util.Objects;

public class K2StringsModel {

    protected String str1;
    protected String str2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K2StringsModel that = (K2StringsModel) o;
        return Objects.equals(str1, that.str1) &&
                Objects.equals(str2, that.str2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(str1, str2);
    }
}
