package org.xdove.ioc.exception;

public class NoSuchBeanException extends RuntimeException{
    public NoSuchBeanException(String message) {
        super(message);
    }
}
