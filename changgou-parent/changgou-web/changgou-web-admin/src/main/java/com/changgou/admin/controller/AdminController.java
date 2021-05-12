package com.changgou.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // 后台管理员登录页面，用户名和密码均为 admin
    @GetMapping("/login")
    public String login() {
        return "adminlogin";
    }

    // 后台管理员管理页面
    @GetMapping("/manage")
    public String manage() {
        return "manage";
    }
    
}
