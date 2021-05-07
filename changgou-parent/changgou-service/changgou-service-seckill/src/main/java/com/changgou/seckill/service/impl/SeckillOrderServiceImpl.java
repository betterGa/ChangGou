package com.changgou.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.seckill.task.MultiThreadingCreateOrder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.SeckillStatus;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;
import tk.mybatis.mapper.entity.Example;

import java.awt.image.BandCombineOp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:SeckillOrder业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private MultiThreadingCreateOrder multiThreadingCreateOrder;

    /**
     * 更改订单状态
     *
     * @param transactionId
     * @param username
     * @param endtime
     */
    @Override
    public void updatePayStatu(String transactionId, String username, String endtime) {
        /** 修改订单状态信息 */
        // 获取订单
        SeckillOrder seckillOrder = JSON.parseObject((String) redisTemplate.boundHashOps("SeckillOrder").get(username),
                SeckillOrder.class);
        if (seckillOrder != null) {
            // 设置状态为已支付
            seckillOrder.setStatus("1");
            seckillOrder.setTransactionId(transactionId);

            // 支付完成时间，格式为yyyyMMddHHmmss
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                Date payTime = simpleDateFormat.parse(endtime);
                seckillOrder.setPayTime(payTime);

                // 更新到数据库
                seckillOrderMapper.insert(seckillOrder);

                // 删除 Redis 中的订单记录
                redisTemplate.boundHashOps("SeckillOrder").delete(username);

                // 删除 Redis 排队信息
                redisTemplate.boundHashOps("UserQueueCount").delete(username);
                redisTemplate.boundHashOps("UserQueueStatus").delete(username);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除订单
     *
     * @param username
     */
    @Override
    public void deleteOrder(String username) {
        // 删除订单
        redisTemplate.boundHashOps("SeckillOrder").delete(username);

        // 查询用户排队信息
        SeckillStatus userQueueStatus = (SeckillStatus) redisTemplate.boundHashOps("UserQueueStatus").get(username);

        // 删除排队信息
        redisTemplate.boundHashOps("UserQueueCount").delete(username);
        redisTemplate.boundHashOps("UserQueueStatus").delete(username);

        // 回滚库存
        // Redis 递增
        String namespace = "SeckillGoods_" + userQueueStatus.getTime();
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(namespace)
                .get(userQueueStatus.getGoodsId());

        // 如果商品为空，到数据库查询
        if (seckillGoods == null) {

            seckillGoods = seckillGoodsMapper.selectByPrimaryKey(userQueueStatus.getGoodsId());
            // 更新数据库内存
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
        } else {
            seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            redisTemplate.boundHashOps(namespace).put(seckillGoods.getId(), seckillGoods);

            redisTemplate.boundListOps("SeckillGoodsCountList" + seckillGoods.getId())
                    .leftPush(seckillGoods.getId());
        }
    }

    /**
     * 抢单状态查询
     *
     * @param username
     * @return
     */
    @Override
    public SeckillStatus queryStatus(String username) {
        return JSON.parseObject((String) redisTemplate.boundHashOps("UserQueueStatus").get(username),
                SeckillStatus.class);
    }

    /**
     * SeckillOrder条件+分页查询
     *
     * @param seckillOrder 查询条件
     * @param page         页码
     * @param size         页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<SeckillOrder> findPage(SeckillOrder seckillOrder, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(seckillOrder);
        //执行搜索
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectByExample(example));
    }

    /**
     * SeckillOrder分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<SeckillOrder> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<SeckillOrder>(seckillOrderMapper.selectAll());
    }

    /**
     * SeckillOrder条件查询
     *
     * @param seckillOrder
     * @return
     */
    @Override
    public List<SeckillOrder> findList(SeckillOrder seckillOrder) {
        //构建查询条件
        Example example = createExample(seckillOrder);
        //根据构建的条件查询数据
        return seckillOrderMapper.selectByExample(example);
    }


    /**
     * SeckillOrder构建查询对象
     *
     * @param seckillOrder
     * @return
     */
    public Example createExample(SeckillOrder seckillOrder) {
        Example example = new Example(SeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (seckillOrder != null) {
            // 主键
            if (!StringUtils.isEmpty(seckillOrder.getId())) {
                criteria.andEqualTo("id", seckillOrder.getId());
            }
            // 秒杀商品ID
            if (!StringUtils.isEmpty(seckillOrder.getSeckillId())) {
                criteria.andEqualTo("seckillId", seckillOrder.getSeckillId());
            }
            // 支付金额
            if (!StringUtils.isEmpty(seckillOrder.getMoney())) {
                criteria.andEqualTo("money", seckillOrder.getMoney());
            }
            // 用户
            if (!StringUtils.isEmpty(seckillOrder.getUserId())) {
                criteria.andEqualTo("userId", seckillOrder.getUserId());
            }
            // 创建时间
            if (!StringUtils.isEmpty(seckillOrder.getCreateTime())) {
                criteria.andEqualTo("createTime", seckillOrder.getCreateTime());
            }
            // 支付时间
            if (!StringUtils.isEmpty(seckillOrder.getPayTime())) {
                criteria.andEqualTo("payTime", seckillOrder.getPayTime());
            }
            // 状态，0未支付，1已支付
            if (!StringUtils.isEmpty(seckillOrder.getStatus())) {
                criteria.andEqualTo("status", seckillOrder.getStatus());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(seckillOrder.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", seckillOrder.getReceiverAddress());
            }
            // 收货人电话
            if (!StringUtils.isEmpty(seckillOrder.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", seckillOrder.getReceiverMobile());
            }
            // 收货人
            if (!StringUtils.isEmpty(seckillOrder.getReceiver())) {
                criteria.andEqualTo("receiver", seckillOrder.getReceiver());
            }
            // 交易流水
            if (!StringUtils.isEmpty(seckillOrder.getTransactionId())) {
                criteria.andEqualTo("transactionId", seckillOrder.getTransactionId());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        seckillOrderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改SeckillOrder
     *
     * @param seckillOrder
     */
    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 秒杀下单
     *
     * @param
     */
    @Override
    public boolean add(String time, Long id, String username) {

        // 记录用户排队次数
        Long userQueueCount = redisTemplate.boundHashOps("UserQueueCount").increment(username, 1);

        // 重复排队
        if (userQueueCount > 1) {
            throw new RuntimeException(String.valueOf(StatusCode.REPERROR));
        }


        // 创建排队对象
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, id, time);

        // 排队, Redis 实现队列
        redisTemplate.boundListOps("SeckillOrderQueue").leftPush(JSON.toJSONString(seckillStatus));

        // 用于查询订单状态
        redisTemplate.boundHashOps("UserQueueStatus").put(username, JSON.toJSONString(seckillStatus));

        // 异步（多线程）执行
        multiThreadingCreateOrder.createOrder();
        return true;
    }

    /**
     * 根据ID查询SeckillOrder
     *
     * @param id
     * @return
     */
    @Override
    public SeckillOrder findById(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询SeckillOrder全部数据
     *
     * @return
     */
    @Override
    public List<SeckillOrder> findAll() {
        return seckillOrderMapper.selectAll();
    }
}
