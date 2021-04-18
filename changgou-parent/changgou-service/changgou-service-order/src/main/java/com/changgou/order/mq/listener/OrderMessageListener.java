package com.changgou.order.mq.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Map;

@Component
@RabbitListener(queues = "${mq.pay.queue.order}")
public class OrderMessageListener {

    @Autowired
    OrderService orderService;

    /**
     * 支付结果监听
     */
    @RabbitHandler
    public void getMessage(String message) throws ParseException {

        // 支付结果
        Map<String,String> resultMap = JSON.parseObject(message, Map.class);

        // 输出监听到的消息
        System.out.println(resultMap);

        // 通信标识
        String returnCode=resultMap.get("return_code");

        if(returnCode.equals("SUCCESS")){
            // 业务结果
            String resultCode = resultMap.get("result_code");

            // 订单号
            String outTradeNo = resultMap.get("out_trade_no");

            // 支付成功
            if(resultCode.equals("SUCCESS")){

                // 更改订单信息
                orderService.updateStatus(outTradeNo,resultMap.get("time_end"),resultMap.get("transaction_id"));


            }else {
                // 如果支付失败，需要关闭支付，取消订单，回滚库存
                orderService.deleteOrder(outTradeNo);
            }
        }
    }
}
