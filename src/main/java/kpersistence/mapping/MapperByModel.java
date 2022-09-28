package kpersistence.mapping;

import kpersistence.modelsMaster.mapping.FieldProperties;
import kpersistence.modelsMaster.mapping.TableModelForMapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public abstract class MapperByModel<T> {

    protected Class<T> klass;

    public MapperByModel(Class<T> klass) {
        this.klass = klass;
    }

    protected abstract TableModelForMapping getMappingModel();

    public T mapRow(ResultSet rs, int i) {

        TableModelForMapping mappingModel = getMappingModel();

        List<FieldProperties> mainFieldPropertiesList = mappingModel.getMainBunchList();
        Map<Class<?>, List<FieldProperties>> parentClassPropertiesMap = mappingModel.getParentClassPropertiesMap();
        Map<Class<?>, Field> parentClassToMainFieldMap = mappingModel.getParentClassToMainFieldMap();

        try {
            T mainDTO = klass.getConstructor().newInstance();

            mainFieldPropertiesList.forEach(fieldProperty -> fieldProperty.setData(rs, mainDTO));

            for (Class<?> parentClass : parentClassPropertiesMap.keySet()) {
                Object parentTDO = parentClass.getConstructor().newInstance();
                List<FieldProperties> props = parentClassPropertiesMap.get(parentClass);

                props.forEach(fieldProperty -> fieldProperty.setData(rs, parentTDO));

                if (mappingModel.checkParentForNonNullFields(parentTDO)) {
                    parentClassToMainFieldMap.get(parentClass).set(mainDTO, parentTDO);
                }
            }

            return mainDTO;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
