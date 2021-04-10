package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;
import entity.Result;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param num
     * @param id
     * @param username
     */
    void add(Integer num, Long id,String username);

    /**
     * 查询购物车商品列表
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
}
