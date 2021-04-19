package com.changgou.weixinpay;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("weixinpay")
@RequestMapping(value = "/weixin/pay")
public interface WeiXinPayFeign {

    /**
     * 取消订单
     * @param outTradeNo
     * @return
     */

    @RequestMapping(value = "/cancel/order")
    public Result cancelOrder(@RequestParam String outTradeNo);
}
