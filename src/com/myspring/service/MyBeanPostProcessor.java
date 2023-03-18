package com.myspring.service;

import com.myspring.spring.BeanPostProcessor;
import com.myspring.spring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public void postBeanInitialize(Object object, String name) {
        System.out.println("finished " + name + "initialize." + object);
    }
}
