package com.changgou.user.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.User;

import java.util.List;

@FeignClient(value = "user")
@RequestMapping(value = "/user")
public interface UserFeign {

    // 查找所有用户信息
    @GetMapping
    public Result<List<User>> findAll();


    /**
     * 查询用户信息
     *
     * @param id
     * @return
     */
    @GetMapping({"/load/{id}"})
    Result<User> findById(@PathVariable String id);


    /**
     * 增加用户积分
     *
     * @param points
     * @return
     */
    @GetMapping(value = "/points/add")
    public Result addPoints(@RequestParam Integer points);

}
