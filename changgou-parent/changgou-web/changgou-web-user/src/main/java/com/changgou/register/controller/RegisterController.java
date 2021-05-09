package com.changgou.register.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


    @Controller
    @RequestMapping("/user")
    public class RegisterController {
        @CrossOrigin
        @GetMapping("/register")
        public String search() {
            return "register";
        }
    }