package kpersistence.v2.mapping;

import kpersistence.v2.modelsMaster.ModelsMaster;
import kpersistence.v2.modelsMaster.mapping.TableModelForMapping;

public class MapperAllDataByModel<T> extends MapperByModel<T> {

    public MapperAllDataByModel(Class<T> klass) {
        super(klass);
    }

    protected TableModelForMapping getMappingModel() {
        return ModelsMaster.getMappingAllDataModel(klass);
    }
}
