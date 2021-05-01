package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.service.SeckillOrderService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 秒杀订单监听
 */
@Component
@RabbitListener(queues = "${mq.pay.queue.seckillorder}")
public class SeckillMessageListener {

    @Autowired
    SeckillOrderService seckillOrderService;


    @RabbitHandler
    public void getMessage(String message) throws Exception {
        System.out.println("Message："+message);
        // 将支付信息转成 Map
        Map<String, String> resultMap = JSON.parseObject(message, Map.class);
        System.out.println(resultMap);
        String return_code = resultMap.get("return_code");

        // 获取订单号
        String outtradeno = resultMap.get("out_trade_no");

        // 获取事务id
        String transactionid = resultMap.get("transaction_id");

        // 获取支付完成时间
        String timeEnd = resultMap.get("time_end");

        // 获取自定义数据
        String attach = resultMap.get("attach");
        Map<String, String> attachMap = JSON.parseObject(attach, Map.class);
        String username = attachMap.get("username");


        if ("SUCCESS".equals(return_code)) {
            String result_code = resultMap.get("result_code");
            if ("SUCCESS".equals(result_code)) {
                // 秒杀订单支付成功后，需要修改订单状态,清理用户排队信息
                seckillOrderService.updatePayStatu(transactionid, username, timeEnd);
            } else {
                // 支付失败，需要删除订单，回滚库存
                seckillOrderService.deleteOrder(username);
            }
        }
    }
}
