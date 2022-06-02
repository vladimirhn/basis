package kpersistence.v2.mapping;

import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.mapping.TableModelForMapping;

public class MapperLabelsByModel<T> extends MapperByModel<T> {

    public MapperLabelsByModel(Class<T> klass) {
        super(klass);
    }

    protected TableModelForMapping getMappingModel() {
        return ModelsMaster.getMappingLabelsModel(klass);
    }
}
