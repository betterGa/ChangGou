package com.changgou.seckill.task;


import com.alibaba.fastjson.JSON;
import com.changgou.order.feign.OrderFeign;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import entity.IdWorker;
import entity.SeckillStatus;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MultiThreadingCreateOrder {


    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 多线程下单操作
     */
    @Async
    public void createOrder() {
        try {
            System.out.println("准备执行下单......");
            // 睡眠
            Thread.sleep(10000);

            // 从队列中获取用户排队信息,先抢先下单
            SeckillStatus seckillStatus = JSON.parseObject((String) redisTemplate.boundListOps("SeckillOrderQueue").
                            rightPop(),
                    SeckillStatus.class);
            if (seckillStatus == null) {
                return;
            }

            // 抢单所属时间段
            String time = seckillStatus.getTime();
            // 商品id
            Long id = seckillStatus.getGoodsId();
            // 用户名
            String username = seckillStatus.getUsername();
            /***
             *当没有库存时，需要清理排队信息
             */
            Object goods = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId())
                    .rightPop();

            if (goods == null) {
                clearUserQueue(username);
                return;
            }


            // 查询秒杀商品
            String nameSpace = "SeckillGoods_" + time;
            SeckillGoods seckillGoods = JSON.parseObject((String) redisTemplate.boundHashOps(nameSpace)
                            .get(id.toString()),
                    SeckillGoods.class);
            // 判断有无库存
            if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
                throw new RuntimeException("已售罄!");
            }
            // 创建订单对象
            SeckillOrder seckillOrder = new SeckillOrder();

            seckillOrder.setId(idWorker.nextId());

            seckillOrder.setSeckillId(id);
            seckillOrder.setMoney(seckillGoods.getCostPrice());
            seckillOrder.setUserId(username);
            seckillOrder.setCreateTime(new Date());
            // 订单状态：未支付
            seckillOrder.setStatus("0");
            // 将订单对象存储到 Redis 中
            // 一个用户只允许有一个未支付秒杀订单
            redisTemplate.boundHashOps("SeckillOrder").put(username, JSON.toJSONString(seckillOrder));
            // 库存递减
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
            Thread.sleep(10000);

            // 解决库存不精准问题
            Long size = redisTemplate.boundListOps("SeckillGoodsCountList_" + seckillStatus.getGoodsId())
                    .size();
            if (size <= 0) {

                seckillGoods.setStockCount(size.intValue());

               /* // 如果商品是最后一个，需要将 Redis 中将该商品删除，并将数据同步到 Mysql
                if (seckillGoods.getStockCount() <= 0) {
                */

                // 同步到数据库
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
                redisTemplate.boundHashOps(nameSpace).delete(id);
            } else {
                // 同步数据到 Redis
                redisTemplate.boundHashOps(nameSpace).put(id.toString(), JSON.toJSONString(seckillGoods));

                /**
                 * 更新订单状态
                 */
                seckillStatus.setOrderId(seckillOrder.getId());
                // 支付金额
                seckillStatus.setMoney(Float.valueOf(seckillGoods.getCostPrice()));
                // 待付款
                seckillStatus.setStatus(2);
                redisTemplate.boundHashOps("UserQueueStatus").put(username, JSON.toJSONString(seckillStatus));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                System.out.println("下单信息发送时间：" + simpleDateFormat.format(new Date()));

                // 发送消息给 queue1
                rabbitTemplate.convertAndSend("delaySeckillQueue",
                        (Object) JSON.toJSONString(seckillStatus),
                        new MessagePostProcessor() {
                            @Override
                            public Message postProcessMessage(Message message) throws AmqpException {
                                message.getMessageProperties().setExpiration("10000");
                                return message;
                            }
                        });
            }
            System.out.println("10 秒钟后下单完成!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 清理用户排队抢单信息
     */
    public void clearUserQueue(String username) {
        // 排队标识
        redisTemplate.boundHashOps("UserQueueCount").delete(username);

        // 排队信息清理
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);
    }
}
