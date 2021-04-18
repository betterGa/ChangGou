package com.changgou.order.mq.listener;


import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 延时队列监听
 */
@Component
@RabbitListener(queues = "orderListenerQueue")
public class DelayMessageListener {

    @RabbitHandler
    public void delayMessage(String message) {

        System.out.println("监听到的消息" + message);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("监听到消息时间" + simpleDateFormat.format(new Date()));
    }
}
