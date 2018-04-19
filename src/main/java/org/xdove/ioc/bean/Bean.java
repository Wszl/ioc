package org.xdove.ioc.bean;

public class Bean {
    private String  name;
    private Class   klass;
    private Object  instance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getKlass() {
        return klass;
    }

    public void setKlass(Class klass) {
        this.klass = klass;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + name + '\'' +
                ", klass=" + klass +
                ", instance=" + instance +
                '}';
    }
}
