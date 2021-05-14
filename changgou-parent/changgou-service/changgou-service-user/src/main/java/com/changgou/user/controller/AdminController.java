package com.changgou.user.controller;

import com.changgou.user.pojo.Store;
import com.changgou.user.service.AdminService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    // 查看所有商家列表
    @GetMapping("/storeList")
    public Result showList(){
        List<Store> storeList=adminService.showList();
        return new Result(true,StatusCode.OK,"查询商家列表成功",storeList);
    }

    // 查看未入驻商家列表
    @GetMapping("/unCenter")
    public Result unCenterList() {
        List<Store> storeList = adminService.unCenterList();
        return new Result(true, StatusCode.OK, "查询成功", storeList);
    }

    // 查看已入驻商家列表
    @GetMapping("/center")
    public Result centerList() {
        List<Store> storeList = adminService.centerList();
        return new Result(true, StatusCode.OK, "查询成功", storeList);
    }

    @CrossOrigin
    // 允许入驻
    @GetMapping("/permitCenter")
    public Result permitCenter(@RequestParam(value = "storeName")String storeName){
        adminService.permitCenter(storeName);
        return new Result(true,StatusCode.OK,"允许入驻成功");
    }

    // 修改商家使用状态
    @CrossOrigin
    @GetMapping("/restrict")
    public Result restrice(@RequestParam(value = "storeName")String storeName){
        adminService.restrict(storeName);
        return new Result(true,StatusCode.OK,"修改使用状态成功");
    }

   /* // 查看所有用户信息
    @CrossOrigin
    @GetMapping("/")*/


}
