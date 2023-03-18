package com.myspring.service;

import com.myspring.spring.Autowire;
import com.myspring.spring.BeanNameAware;
import com.myspring.spring.Component;

@Component("userService")
public class UserService implements BeanNameAware {
    @Autowire
    OrderService orderService;
    String orderBeanName;

    @Override
    public void setBeanName(String name) {
        this.orderBeanName = name;
    }

    public void test() {
        System.out.println(orderBeanName+ ":" + orderService);
    }
}
