package kmodels;

import kcollections.CollectionFactory;
import kcollections.KList;

import java.util.HashMap;
import java.util.Map;

public class IdLabelWithParentList {

    private KList<IdLabelWithParent> data;

    public IdLabelWithParentList(KList<IdLabelWithParent> data) {
        this.data = data;
    }

    public Map<IdLabel, KList<IdLabel>> groupByParent() {

        Map<IdLabel, KList<IdLabel>> result = new HashMap<>();

        data.groupBy(IdLabelWithParent::extractParent)
            .forEach((parent, idLabelWithParents) -> result.put(parent, idLabelWithParents.mapEachBy(IdLabelWithParent::extractEntity)));

        return result;
    }

    public KList<IdLabelWithParent> getData() {
        return data != null ? data : CollectionFactory.makeList();
    }

    public void setData(KList<IdLabelWithParent> data) {
        this.data = data;
    }
}
