package com.myspring.service;

import com.myspring.spring.MyApplicationContext;

public class Main {
    public static void main(String[] args) {
        MyApplicationContext myApplicationContext = new MyApplicationContext(AppConfig.class);
        UserService userService = (UserService) myApplicationContext.getBean("userService");

    }
}