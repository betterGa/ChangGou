package com.changgou.order.controller;

import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;

    /**
     * 添加商品到购物车
     * @param num 商品数量
     * @param id 商品 ID
     * @return
     */
    @GetMapping("/add")
    public Result add(@RequestParam(value = "num") Integer num, @RequestParam(value = "id") Long id){
        // 解析令牌
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        System.out.println(userInfo);

        // 获取用户登录名
        //String username="jia";
        String username=userInfo.get("username");

        cartService.add(num,id,username);
        return new Result(true, StatusCode.OK,"加入购物车成功");

    }

    @GetMapping("/list")
    public Result<List<OrderItem>> list(){

        // 解析令牌
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        System.out.println(userInfo);

        // 获取用户登录名
        //String username="jia";
        String username=userInfo.get("username");

        // 查询购物车列表
        List<OrderItem> orderItems=cartService.list(username);
        return new Result<>(true,StatusCode.OK,"购物车列表查询成功",orderItems);
    }

    // 删除购物车中商品
    @GetMapping("/delete/{skuId}")
    public Result delete(@PathVariable("skuId")Long skuid){
        // 解析令牌
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        System.out.println(userInfo);

        // 获取用户登录名
        //String username="jia";
        String username=userInfo.get("username");
        cartService.delete(username,skuid);
        return new Result(true,StatusCode.OK,"删除商品成功");
    }
}
