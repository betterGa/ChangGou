package com.jia;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {
    // 信息发送队列
    public static final String QUEUE_MESSAGE = "queue.message";

    // 交换机
    public static final String DLX_EXCHANGE = "dlx.exchange";

    // 延迟队列
    public static final String QUEUE_MESSAGE_DELAY = "queue.message.delay";

    @Bean
    public Queue messageQueue() {
        return new Queue(QUEUE_MESSAGE, true);
    }

    @Bean
    public Queue delayMessage() {
        return QueueBuilder.durable(QUEUE_MESSAGE_DELAY)
                // 消息超时进入死信队列，绑定死信交换机
                .withArgument("x-dead-letter-exchange",
                        DLX_EXCHANGE)

                // 绑定交换机
                .withArgument("x-dead-letter-routing-key",
                        QUEUE_MESSAGE)
                .build();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // 交换机与队列绑定
    @Bean
    public Binding basicBinding(Queue messageQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(messageQueue)
                .to(directExchange)
                .with(QUEUE_MESSAGE);
    }
}
