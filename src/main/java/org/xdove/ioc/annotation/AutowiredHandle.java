package org.xdove.ioc.annotation;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AutowiredHandle {
    
    private static AutowiredHandle instance = null;
    
    private AutowiredHandle() {}

    public List<Field> handleClass(Class klass) {
        Objects.requireNonNull(klass);
        return filter(klass.getDeclaredFields());
    }

    private List<Field> filter(Field[] fields) {
        var annotationFields = new LinkedList<Field>();
        for (var field : fields) {
            if (Objects.nonNull(field.getAnnotation(Autowired.class))) {
                annotationFields.add(field);
            }
        }
        return annotationFields;
    }

    public static AutowiredHandle getInstance() {
        if (instance == null) {
            synchronized (AutowiredHandle.class) {
                if (instance == null) {
                    instance = new AutowiredHandle();
                }
            }
        }
        return instance;
    }
}
