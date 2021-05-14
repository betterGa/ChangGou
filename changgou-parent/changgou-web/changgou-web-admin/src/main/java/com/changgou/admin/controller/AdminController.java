package com.changgou.admin.controller;

import com.changgou.user.feign.AdminFeign;
import com.changgou.user.feign.UserFeign;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminFeign adminFeign;

    @Autowired
    UserFeign userFeign;

    // 后台管理员登录页面，用户名和密码均为 admin
    @GetMapping("/login")
    public String login() {
        return "adminlogin";
    }

    // 后台管理员管理页面
    @GetMapping("/manage")
    public String manage(Model model) {
        // 未入驻商家列表
        Result unCenterResult=adminFeign.unCenterList();

        // 已入驻商家列表
        Result centerResult=adminFeign.centerList();

        // 所有商家列表
        Result storeList=adminFeign.showList();

        // 所有用户列表
        Result userList=userFeign.findAll();

        model.addAttribute("unCenterList",unCenterResult.getData());
        model.addAttribute("centerList",centerResult.getData());
        model.addAttribute("storeList",storeList.getData());
        model.addAttribute("userList",userList.getData());

        return "manage";
    }



}
