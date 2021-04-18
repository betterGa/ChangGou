package com.changgou.order.mq.queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延时队列配置
 */

@Configuration
public class QueueConfig {

    // 创建 queue1
    @Bean
    public Queue orderDelayQueue(){


        return QueueBuilder.durable("orderDelayQueue")

                // queue1 消息过期，进入到死信【没被读取的消息】队列，需要绑定交换机
                .withArgument("x-dead-letter-exchange","orderListenerExchange")

                // queue1 消息过期，会路由到 queue2
                .withArgument("x-dead-letter-routing-key","orderListenerQueue")
                .build();

    }

    // 创建 queue2
    @Bean
    public Queue orderListenerQueue(){
        // 持久化
        return new Queue("orderListenerQueue",true);

    }

    @Bean
    public Exchange orderListenerExchange(){
        return new DirectExchange("orderListenerExchange");
    }


    // 绑定
    @Bean
    public Binding binding(Queue orderListenerQueue,Exchange orderListenerExchange){
        return BindingBuilder.bind(orderListenerQueue)
                .to(orderListenerExchange)
                .with("orderListenerQueue")
                .noargs();
    }
}
