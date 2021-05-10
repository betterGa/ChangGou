package com.changgou.store.controller;

import com.changgou.store.pojo.Store;
import com.changgou.store.service.StoreService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    StoreService storeService;

    @CrossOrigin
    @PostMapping("/register")
    public Result register(@RequestBody Store store) {
        boolean isSuccess = storeService.add(store);
        if (isSuccess) {
            return new Result(true, StatusCode.OK, "插入商家记录成功");
        } else return new Result(false, StatusCode.ERROR, "用户名已存在，插入失败");
    }

    @CrossOrigin
    @GetMapping("/login")
    public Result login(@RequestParam(value = "storeName") String storename,
                        @RequestParam(value = "password") String password) {
        Store store=storeService.checkLogin(storename, password);
        if (store!=null) {
            return new Result(true, StatusCode.OK, "登录成功!",store);
        } else {
            return new Result(false, StatusCode.ERROR, "登录失败，查询无果!");
        }
    }
}
