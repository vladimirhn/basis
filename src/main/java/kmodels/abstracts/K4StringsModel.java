package kmodels.abstracts;

import java.util.Objects;

public class K4StringsModel {

    protected String str1;
    protected String str2;
    protected String str3;
    protected String str4;

    public void initData(String s1, String s2, String s3, String s4) {

        if (str1 != null || str2 != null || str3 != null || str4 != null) {
            throw new IllegalStateException("Only empty model can be filled with initial data.");
        }

        str1 = s1; str2 = s2; str3 = s3; str4 = s4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        K4StringsModel that = (K4StringsModel) o;
        return Objects.equals(str1, that.str1) &&
                Objects.equals(str2, that.str2) &&
                Objects.equals(str3, that.str3) &&
                Objects.equals(str4, that.str4);
    }

    @Override
    public int hashCode() {
        return Objects.hash(str1, str2, str3, str4);
    }
}
