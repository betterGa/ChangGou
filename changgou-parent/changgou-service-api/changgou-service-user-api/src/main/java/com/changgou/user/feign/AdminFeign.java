package com.changgou.user.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "user")
@RequestMapping("/admin")
public interface AdminFeign {

    @GetMapping("/unCenter")
    public Result unCenterList();

    @GetMapping("/center")
    public Result centerList();

    @GetMapping("storeList")
    public Result showList();
    }

