package kmodels;

import kmodels.abstracts.K4StringsModel;

public class IdLabelWithParent extends K4StringsModel {

    public IdLabelWithParent() {}

    //extractors
    public IdLabel extractParent() {
        return new IdLabel(getParentId(), getParentLabel());
    }

    public IdLabel extractEntity() {
        return new IdLabel(getId(), getLabel());
    }

    //getters+setters
    public String getParentId() {
        return str1;
    }
    
    public void setParentId(String parentId) {
        str1 = parentId;
    }
    
    public String getParentLabel() {
        return str2;
    }
    
    public void setParentLabel(String parentLabel) {
        str2 = parentLabel;
    }

    public String getId() {
        return str3;
    }
    
    public void setId(String id) {
        str3 = id;
    }
    
    public String getLabel() {
        return str4;
    }
    
    public void setLabel(String label) {
        str4 = label;
    }
}
