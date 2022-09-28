package kpersistence.mapping;

import kpersistence.modelsMaster.ModelsMaster;
import kpersistence.modelsMaster.mapping.TableModelForMapping;

public class MapperLabelsByModel<T> extends MapperByModel<T> {

    public MapperLabelsByModel(Class<T> klass) {
        super(klass);
    }

    protected TableModelForMapping getMappingModel() {
        return ModelsMaster.getMappingLabelsModel(klass);
    }
}
