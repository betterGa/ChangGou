package com.changgou.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/store")
public class StoreController {
    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}
