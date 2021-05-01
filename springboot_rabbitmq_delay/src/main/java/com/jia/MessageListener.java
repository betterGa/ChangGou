package com.jia;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@RabbitListener(queues = QueueConfig.QUEUE_MESSAGE)
public class MessageListener {
    /**
     * 监听队列
     * @param msg
     */
    @RabbitHandler
    public void msg(@Payload Object msg){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        System.out.println("当前时间"+dateFormat.format(new Date()));
        System.out.println("收到信息："+msg);
    }
}
