package com.changgou.seckill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    // queue1
    @Bean
    public Queue delaySeckillQueue() {
        return QueueBuilder.durable("delaySeckillQueue")
                // 当前队列消息一旦过期，进入到死信队列交换机
                .withArgument("x-dead-letter-exchange",
                        "seckillExchange")
                // 将死信队列的消息路由到指定队列
                .withArgument("x-dead-letter-routing-key",
                        "seckillQueue")
                .build();
    }

    // queue2
    @Bean
    public Queue seckillQueue() {
        return new Queue("seckillQueue");
    }

    // 交换机
    @Bean
    public Exchange seckillExchange() {
        return new DirectExchange("seckillExchange");
    }

    // 队列绑定交换机
    public Binding seckillQueueBindingExchange(Queue seckillQueue,Exchange seckillExchange){
        return BindingBuilder.bind(seckillQueue)
                .to(seckillExchange)
                .with("seckillQueue")
                .noargs();
    }
}
