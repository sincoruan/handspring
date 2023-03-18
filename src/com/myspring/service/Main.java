package com.myspring.service;

import com.myspring.spring.MyApplicationContext;

public class Main {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        UserInterface userService2 = (UserInterface) myApplicationContext.getBean("userService");
        System.out.println(userService2);
        userService2.test();
    }
}