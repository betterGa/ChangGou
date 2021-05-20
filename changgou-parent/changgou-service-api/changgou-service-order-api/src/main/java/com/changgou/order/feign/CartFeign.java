package com.changgou.order.feign;

import com.changgou.order.pojo.OrderItem;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "order")
@RequestMapping(value = "/cart")
public interface CartFeign {

    @GetMapping("/list")
    public Result<List<OrderItem>> list();
}
