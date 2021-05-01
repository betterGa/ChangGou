package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.weixinpay.feign.WeiXinPayFeign;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.SeckillStatus;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 延时订单监听
 * 监听 queue2
 */
@Component
@RabbitListener(queues = "seckillQueue")
public class DelaySeckillMessageListener {

    @Autowired
    WeiXinPayFeign weiXinPayFeign;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    private RedisTemplate redisTemplate;


    @RabbitHandler
    public void getMessage(String message){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        System.out.println("回滚时间：" + simpleDateFormat.format(new Date()));

        // 获取队列中信息
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);

        Object userQueueStatus = redisTemplate.boundHashOps("UserQueueStatus").get(seckillStatus.getUsername());
        // 如果没有排队信息，说明订单已经处理
        // 否则说明尚未完成支付
        if (userQueueStatus!=null) {
            // 关闭微信支付订单
            Result result=weiXinPayFeign.cancelOrder(seckillStatus.getOrderId().toString());
            Map<String,String> resultMap = (Map<String, String>) result.getData();
            if(resultMap!=null&&"SUCCESS".equals(resultMap.get("return_code"))
                    &&"SUCCESS".equals(resultMap.get("result_code"))){
                // 删除订单
                seckillOrderService.deleteOrder(seckillStatus.getUsername());
            }
        }
    }
}
