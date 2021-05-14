package com.changgou.store.controller;

import com.changgou.goods.feign.BrandFeign;
import com.changgou.goods.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private BrandFeign brandFeign;

    @RequestMapping("/register")
    public String register() {
        return "register";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/manage/{storeName}")
    public String manage(@PathVariable(value = "storeName")String storeName,Model model){

        // 商品品牌列表
        List<Brand> brandList=brandFeign.findByStore(storeName).getData();
        model.addAttribute("brandList",brandList);
        model.addAttribute("storename",storeName);
        return "manage";
    }
}
