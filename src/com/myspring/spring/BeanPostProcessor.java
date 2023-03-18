package com.myspring.spring;

public interface BeanPostProcessor {
    public Object postBeanInitialize(Object object, String name);
}
