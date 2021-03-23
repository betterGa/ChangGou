package com.changgou.item.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "item")
@RequestMapping("/page")
public interface PageFeign {

    @RequestMapping("/createHtml/{id}")
    Result createHtml(@PathVariable(name = "id")long id);
}
