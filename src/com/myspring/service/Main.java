package com.myspring.service;

import com.myspring.spring.MyApplicationContext;

public class Main {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        UserService userService1 = (UserService) myApplicationContext.getBean("userService");
        UserService userService2 = (UserService) myApplicationContext.getBean("userService");
        System.out.println(userService1);
        System.out.println(userService2);
        userService1.test();

    }
}