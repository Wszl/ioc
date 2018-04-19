package org.xdove.ioc.annotation;

import java.util.Objects;

public class BeanHandle {

    private static BeanHandle instance = null;

    private BeanHandle() {}

    public boolean handleClass(Class klass) {
        Objects.requireNonNull(klass);
        return klass.getDeclaredAnnotation(Bean.class) != null;
    }

    public static BeanHandle getInstance() {
        if (instance == null) {
            synchronized (BeanHandle.class) {
                if (instance == null) {
                    instance = new BeanHandle();
                }
            }
        }
        return instance;
    }
}
