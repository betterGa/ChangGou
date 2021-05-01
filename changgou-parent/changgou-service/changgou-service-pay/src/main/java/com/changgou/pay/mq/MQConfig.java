package com.changgou.pay.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 在 RabbitMQ 中生成交换机和队列
 */
@Configuration
public class MQConfig {


    // 读取配置文件中的内容对象
    @Autowired
    private Environment environment;

    // 创建队列
    @Bean
    public Queue orderQueue() {
        return new Queue(environment.getProperty("mq.pay.queue.order"));
    }

    // 创建交换机
    @Bean
    public Exchange orderExchange() {

        // 持久化，不自动删除
        return new DirectExchange(environment.getProperty("mq.pay.exchange.order"), true, false);
    }

    // 绑定
    @Bean
    public Binding binding(Queue orderQueue, Exchange orderExchange) {
        return BindingBuilder.bind(orderQueue).to(orderExchange)
                .with(environment.getProperty("mq.pay.routing.key"))
                .noargs();
    }



    /****
     * 秒杀队列创建
     * @return
     */
    // 创建队列
    @Bean
    public Queue orderSeckillQueue() {
        return new Queue(environment.getProperty("mq.pay.queue.seckillorder"));
    }

    // 创建交换机
    @Bean
    public Exchange orderSeckillExchange() {

        // 持久化，不自动删除
        return new DirectExchange(environment.getProperty("mq.pay.exchange.seckillorder"), true, false);
    }

    // 绑定
    @Bean
    public Binding seckillBinding(Queue orderSeckillQueue, Exchange orderSeckillExchange) {
        return BindingBuilder.bind(orderSeckillQueue).to(orderSeckillExchange)
                .with(environment.getProperty("mq.pay.routing.seckillkey"))
                .noargs();
    }
}
