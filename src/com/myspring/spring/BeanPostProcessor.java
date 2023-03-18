package com.myspring.spring;

public interface BeanPostProcessor {
    public void postBeanInitialize(Object object, String name);
}
