package kpersistence.mapping;

import kpersistence.modelsMaster.ModelsMaster;
import kpersistence.modelsMaster.mapping.TableModelForMapping;

public class MapperAllDataByModel<T> extends MapperByModel<T> {

    public MapperAllDataByModel(Class<T> klass) {
        super(klass);
    }

    protected TableModelForMapping getMappingModel() {
        return ModelsMaster.getMappingAllDataModel(klass);
    }
}
