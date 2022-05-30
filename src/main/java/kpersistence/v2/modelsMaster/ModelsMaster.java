package kpersistence.v2.modelsMaster;

import kpersistence.v2.annotations.Table;
import kutils.PackageUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelsMaster {

    class Bunch {
        public TableModelForQueries queryModel;
        public TableModelForMapping mappingModel;
    }

    public static final Map<Class<?>, Bunch> classBunchMap = new LinkedHashMap<>();

    public ModelsMaster(String packageName) {

        System.out.println("Scanning: " + packageName + " for model classes");

        try {

            List<Class<?>> models = PackageUtils.getClassesRecursively(packageName).stream()
                     .filter(klass -> klass.isAnnotationPresent(Table.class))
                     .collect(Collectors.toList());

             models.forEach(klass -> {

                 Bunch bunch = new Bunch();
                 bunch.queryModel = new TableModelForQueries(klass);
                 bunch.mappingModel = new TableModelForMapping(klass);

                 classBunchMap.put(klass, bunch);
             });

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static TableModelForQueries getQueryModel(Class<?> klass) {
         return classBunchMap.get(klass).queryModel;
    }

    public static TableModelForMapping getMappingModel(Class<?> klass) {
        return classBunchMap.get(klass).mappingModel;
    }

}

