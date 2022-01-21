package kmodels;

import kmodels.abstracts.K2StringsModel;

public class IdLabel extends K2StringsModel {

    public IdLabel(String id, String label) {
        setId(id);
        setLabel(label);
    }

    public String getId() {
        return str1;
    }

    public void setId(String id) {
        str1 = id;
    }

    public String getLabel() {
        return str2;
    }

    public void setLabel(String label) {
        str2 = label;
    }
}
