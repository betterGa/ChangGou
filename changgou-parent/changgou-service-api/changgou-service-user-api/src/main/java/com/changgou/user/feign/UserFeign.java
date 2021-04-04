package com.changgou.user.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pojo.User;

@FeignClient(value = "user")
@RequestMapping(value = "/user")
public interface UserFeign{

    /**
     * 查询用户信息
     * @param id
     * @return
     */
    @GetMapping({"/load/{id}"})
    Result<User> findById(@PathVariable String id);
}
