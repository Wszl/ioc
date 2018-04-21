package org.xdove.ioc;

import org.xdove.ioc.exception.NoSuchBeanException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ApplicationContext {

    private BeanFactory beanFactory = BeanFactory.getInstance();

    public void init() throws IOException, ClassNotFoundException {
        beanFactory.init();
    }

    public Object getBean(Class klass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, NoSuchBeanException, IllegalAccessException {
        return beanFactory.getBean(klass);
    }

    public void initBeanClass(Class klass) {
        beanFactory.initBeanClass(klass);
    }


}
