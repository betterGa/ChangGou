package com.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    /**
     * 添加商品到购物车
     *
     * @param num
     * @param id  sku ID
     * @return
     */
    @Override
    public void add(Integer num, Long id, String username) {
        // 当商品数量 <=0 时，需要移除商品
        if (num <= 0) {
            redisTemplate.boundHashOps("cart_" + username).delete(id);

            // 如果此时购物车无商品，需要将购物车也移除
            if (redisTemplate.boundHashOps("cart_" + username).size() <= 0) {
                redisTemplate.delete("cart_" + username);
            }
            return;
        }

        // 查询商品详情
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
        Spu spu = spuResult.getData();

        // 将加入购物车的商品信息封装成 OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(id);
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num * orderItem.getPrice());
        orderItem.setImage(spu.getImage());

        // 存入 Redis
        // 解决 Reids 乱码问题
        /* redisTemplate.boundHashOps("cart_" + username).put(id.toString(), JSON.toJSONString(orderItem));*/
        redisTemplate.boundHashOps("cart_" + username).put(id, orderItem);
    }

    /**
     * 查询购物车商品列表
     *
     * @param username
     * @return
     */
    @Override
    public List<OrderItem> list(String username) {
        return redisTemplate.boundHashOps("cart_" + username).values();

    }

    @Override
    public void delete(String username, Long skuid) {
        redisTemplate.boundHashOps("cart_"+username).delete(skuid);
    }
}
