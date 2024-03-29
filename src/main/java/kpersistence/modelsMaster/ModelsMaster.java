package kpersistence.modelsMaster;

import kpersistence.annotations.Table;
import kpersistence.modelsMaster.mapping.TableModelForAllDataMapping;
import kpersistence.modelsMaster.mapping.TableModelForLabelsMapping;
import kpersistence.modelsMaster.mapping.TableModelForMapping;
import kpersistence.modelsMaster.queries.TableModelForAllDataQueries;
import kpersistence.modelsMaster.queries.TableModelForLabelsQueries;
import kpersistence.modelsMaster.schema.TableDescription;
import kutils.PackageUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelsMaster {

    class Bunch {
        public TableModelForAllDataQueries queryAllDataModel;
        public TableModelForLabelsQueries queryLabelsModel;
        public TableModelForMapping mappingAllDataModel;
        public TableModelForMapping mappingLabelsModel;
    }

    public static final Map<Class<?>, Bunch> classBunchMap = new LinkedHashMap<>();
    public static final List<TableDescription> schemaDescription = new ArrayList<>();

    public ModelsMaster(String packageName) {

        System.out.println("Scanning: " + packageName + " for model classes");

        try {
            List<Class<?>> models = PackageUtils.getClassesRecursively(packageName).stream()
                    .filter(klass -> klass.isAnnotationPresent(Table.class))
                    .collect(Collectors.toList());

            models.forEach(this::addClass);

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void addClass(Class<?> klass) {
        Bunch bunch = new Bunch();
        bunch.queryAllDataModel = new TableModelForAllDataQueries(klass);
        bunch.queryLabelsModel = new TableModelForLabelsQueries(klass);
        bunch.mappingAllDataModel = new TableModelForAllDataMapping(klass);
        bunch.mappingLabelsModel = new TableModelForLabelsMapping(klass);

        classBunchMap.put(klass, bunch);

        schemaDescription.add(new TableDescription(klass));
    }

    public static Map<Class<?>, Bunch> getClassBunchMap() {
        return classBunchMap;
    }

    public static TableModelForAllDataQueries getQueryAllDataModel(Class<?> klass) {
         return classBunchMap.get(klass).queryAllDataModel;
    }

    public static TableModelForLabelsQueries getQueryLabelsModel(Class<?> klass) {
         return classBunchMap.get(klass).queryLabelsModel;
    }

    public static TableModelForMapping getMappingAllDataModel(Class<?> klass) {
        return classBunchMap.get(klass).mappingAllDataModel;
    }
    public static TableModelForMapping getMappingLabelsModel(Class<?> klass) {
        return classBunchMap.get(klass).mappingLabelsModel;
    }

    public static List<TableDescription> getSchemaDescription() {
        return schemaDescription;
    }
}

