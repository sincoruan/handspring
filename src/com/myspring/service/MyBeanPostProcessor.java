package com.myspring.service;

import com.myspring.spring.BeanPostProcessor;
import com.myspring.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postBeanInitialize(Object object, String name) {
        if(name.equals("userService")) {
            Object proxy = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(), object.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("切面打印...");

                    return method.invoke(object, args);
                }
            });
            return proxy;
        }
        System.out.println("finished " + name + "initialize." + object);
        return object;
    }
}
