package com.changgou.item.controller;

import com.changgou.order.feign.CartFeign;
import com.changgou.order.pojo.OrderItem;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartFeign cartFeign;

    @RequestMapping("/showlist")
    public String cartList(Model model) {
    Result<List<OrderItem>> list = cartFeign.list();
      model.addAttribute("cartList", list.getData());
        return "cart";
    }
}
