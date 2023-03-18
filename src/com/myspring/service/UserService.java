package com.myspring.service;

import com.myspring.spring.Autowire;
import com.myspring.spring.Component;

@Component("userService")
public class UserService {
    @Autowire
    OrderService orderService;
}
