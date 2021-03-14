package com.jia.controller;

import com.jia.model.User;
import org.omg.CORBA.portable.ValueInputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * 模板引擎
 */

@Controller
@RequestMapping(value = "/test")
public class TestController {
    @GetMapping(value="/hello")
    public String hello(Model model){
        model.addAttribute("hello","hello thymeleaf");

        // 创建 List<User>，并将它存入 model 中，在页面使用 Thymeleaf 显示
        //集合数据
        List<User> users = new ArrayList<User>();
        users.add(new User(1,"张三","深圳"));
        users.add(new User(2,"李四","北京"));
        users.add(new User(3,"王五","武汉"));
        model.addAttribute("users",users);


        //Map定义
        Map<String,Object> dataMap = new HashMap<String,Object>();
        dataMap.put("No","123");
        dataMap.put("address","深圳");
        model.addAttribute("dataMap",dataMap);

        // 数组定义
        String[] names = {"张三","李四","王五"};
        model.addAttribute("names",names);

        // 日期定义
        model.addAttribute("now",new Date());

        model.addAttribute("age",22);



        return  "demo";
    }
}