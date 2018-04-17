package org.xdove.ioc;

import org.xdove.ioc.annotation.AutowiredHandle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BeanFactory {

    private static BeanFactory factory;

    private Map<Class, Object> cache = new HashMap<>();

    private BeanFactory() {}

    public static BeanFactory getInstance() {
        if (factory == null) {
            BeanFactory.factory = new BeanFactory();
        }
        return factory;
    }

    public Object getBean(Class clazz) throws NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Objects.requireNonNull(clazz);

        Object bean = cache.get(clazz);
        if (bean == null) {
            bean = initBean(clazz);
        }
        return bean;
    }

    public Object initBean(Class clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Objects.requireNonNull(clazz);
        cache.put(clazz, clazz.getConstructor().newInstance());
        return getBean(clazz);
    }

    public void inject(Object receive) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Objects.requireNonNull(receive);
        var handle = AutowiredHandle.getInstance();
        for (Field field : handle.handleClass(receive.getClass())) {
            var destInstance = getBean(field.getType());
            if (destInstance == null) {
                throw new NoClassDefFoundError();
            }
            var receiveInstance = getBean(receive.getClass());
            field.setAccessible(true);
            field.set(receiveInstance, destInstance);
        }
    }

}
