package com.myspring.service;

import com.myspring.spring.Autowire;
import com.myspring.spring.BeanNameAware;
import com.myspring.spring.Component;
import com.myspring.spring.InitializingBean;

@Component("userService")
public class UserService implements BeanNameAware, InitializingBean {
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

    @Override
    public void afterPropertiesSet() {
        System.out.println(this.getClass().getName() + "  all properties have been set.");
    }
}
