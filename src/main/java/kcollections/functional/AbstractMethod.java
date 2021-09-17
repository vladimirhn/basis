package kcollections.functional;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public interface AbstractMethod extends Serializable {

    default String getMethodName() {

        String methodName = "UnidentifiedMethod";

        try {
            Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);

            Object tmp = writeReplace.invoke(this);
            if (tmp instanceof SerializedLambda) {
                SerializedLambda sl = (SerializedLambda) tmp;
                String name = sl.getImplClass() + "::" + sl.getImplMethodName();
                int slashIndex = name.lastIndexOf("/");
                methodName = name.substring(slashIndex + 1);
            }

        } catch (Exception ignored) {}

        return methodName;
    }
}
