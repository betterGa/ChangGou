package com.changgou.order.mq.listener;


import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.service.OrderService;
import com.changgou.weixinpay.WeiXinPayFeign;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 延时队列监听
 */
@Component
@RabbitListener(queues = "orderListenerQueue")
public class DelayMessageListener {

    @Autowired
    OrderService orderService;

    @Autowired
    WeiXinPayFeign weiXinPayFeign;

    /***
     * 监听队列消息
     * @param message 订单id
     */
    @RabbitHandler
    public void delayMessage(String message) {

        System.out.println("监听到的消息" + message);
        // 获取监听到的 Order 信息
        Order order=orderService.findById(message);

        if(order.getPayStatus().equals("0")){
            // 未支付
            weiXinPayFeign.cancelOrder(message);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("监听到消息时间" + simpleDateFormat.format(new Date()));
    }
}
