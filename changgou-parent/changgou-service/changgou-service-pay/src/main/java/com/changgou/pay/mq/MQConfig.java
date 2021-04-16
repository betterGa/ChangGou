package com.changgou.pay.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.TreeMap;

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
        return new Queue(environment.getProperty("mq.pay.exchange"));
    }

    // 创建交换机
    @Bean
    public Exchange orderExchange() {

        // 持久化，不自动删除
        return new DirectExchange(environment.getProperty("mq.pay.exchange.order"), true, false);
    }

    // 绑定
    public Binding binding(Queue queue, Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(environment.getProperty("mq.pay.routing.key"))
                .noargs();
    }
}
