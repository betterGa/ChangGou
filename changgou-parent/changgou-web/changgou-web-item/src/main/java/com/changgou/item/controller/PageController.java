package com.changgou.item.controller;

import com.changgou.item.service.PageService;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.OrderItem;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private PageService pageService;
    @CrossOrigin
    @RequestMapping("/createHtml/{id}")
    public Result createPageHtml(@PathVariable(name = "id") Long id) {
        pageService.createPageHtml(id);
        return new Result(true, StatusCode.OK, "ok");
    }
}
