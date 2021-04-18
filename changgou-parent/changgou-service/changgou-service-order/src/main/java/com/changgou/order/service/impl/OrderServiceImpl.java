package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.dao.OrderItemMapper;
import com.changgou.order.dao.OrderMapper;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.OrderService;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Order业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 修改订单状态
     *
     * @param outradeno
     * @param paytime
     * @param transcationid
     */
    @Override
    public void updateStatus(String outradeno, String paytime, String transcationid) throws ParseException {

        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(outradeno);

        // 时间转换
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        // 获取支付时间
        Date payTime = simpleDateFormat.parse(paytime);

        // 修改订单信息
        order.setPayTime(payTime);
        order.setPayStatus("1");
        order.setTransactionId(transcationid);

        orderMapper.updateByPrimaryKey(order);


    }

    /**
     * 删除订单
     *
     * @param outradeno
     */
    @Override
    public void deleteOrder(String outradeno) {
        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(outradeno);

        // 支付失败
        order.setOrderStatus("2");

        orderMapper.updateByPrimaryKey(order);

        // 回滚库存，需要调用 goods 微服务

        // 还需要让微信支付服务器关闭订单


    }

    /**
     * 添加订单实现
     *
     * @param order
     */
    @Override
    public void addOrder(Order order) {
        // 订单主键
        order.setId(String.valueOf(idWorker.nextId()));

        // 总数量
        int totolNum = 0;

        // 总金额
        int totalMoney = 0;

        // 获取订单（购物车）明细
        List<OrderItem> orderItem = new ArrayList<OrderItem>();


        System.out.println(redisTemplate.boundHashOps("cart_" + order.getUsername()).values());


        // 获取勾选商品 ID，
        for (Long skuId : order.getSkuIds()) {

            OrderItem selectedItem = (OrderItem) redisTemplate.boundHashOps("cart_" + order.getUsername()).get(skuId);

            // 将勾选商品加入集合
            orderItem.add(selectedItem);

            // 将勾选商品从购物车中移除
            redisTemplate.boundHashOps("cart_" + order.getUsername()).delete(skuId);
        }

        for (OrderItem item : orderItem) {

            // 价格校验
            /*int price = orderItemMapper.selectByPrimaryKey(item.getId()).getPrice();
            if (price == item.getMoney()) {*/
            // 总金额
            totalMoney += item.getMoney();
            /*  } else totalMoney += price;*/

            // 总数量
            totolNum += item.getNum();
        }

        // 订单商品总数目 = 每个商品数量之和
        order.setTotalNum(totolNum);

        // 订单总金额 = 每个商品金额之和
        order.setTotalMoney(totalMoney);

        // 订单实付金额 = 每个商品实付金额之和
        order.setPayMoney(totalMoney);

        // 订单优惠金额 = 总金额 - 实付金额，暂时为 0
        order.setPreMoney(0);

        // 订单创建时间
        order.setCreateTime(new Date());

        // 订单修改时间
        order.setUpdateTime(order.getCreateTime());

        // 订单评价状态，0 表示未评价,1 表示已评价
        order.setBuyerRate("0");

        // 订单来源，1 表示 Web
        order.setSourceType("1");

        // 订单状态，0 表示未完成，1 表示已完成，2 表示已退货
        order.setOrderStatus("0");

        // 订单支付状态，0 表示未支付，1 表示已支付，2 表示支付失败
        order.setPayStatus("0");

        // 订单删除状态，0 表示未删除
        order.setIsDelete("0");

        // 订单发货状态，0 表示未发货，1 表示已发货，2 表示已收货
        order.setConsignStatus("0");

        // 先添加订单信息
        orderMapper.insertSelective(order);

        // 封装库存递减 Map
        HashMap<String, String> decrMap = new HashMap<>();

        // 再添加订单明细信息
        for (OrderItem item : orderItem) {
            item.setId(String.valueOf(idWorker.nextId()));
            // 是否退货,0 表示未退货，1 表示已退货
            item.setIsReturn("0");
            item.setOrderId(order.getId());
            // 退货状态，0 表示未退货
            item.setIsReturn("0");

            // 封装库存递减参数
            decrMap.put(item.getSkuId().toString(), item.getNum().toString());

            orderItemMapper.insertSelective(item);
        }

        // 库存递减
        skuFeign.decrCount(decrMap);

        // 用户增加积分，积分表示用户活跃度 +1
        userFeign.addPoints(1);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("输出订单时间" + simpleDateFormat.format(new Date()));

        rabbitTemplate.convertAndSend("orderDelayQueue", (Object) order.getId(),
                new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 设置延时读取
                message.getMessageProperties().setExpiration("10000");
                return message;
            }
        });
    }

    /**
     * Order条件+分页查询
     *
     * @param order 查询条件
     * @param page  页码
     * @param size  页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Order> findPage(Order order, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(order);
        //执行搜索
        return new PageInfo<Order>(orderMapper.selectByExample(example));
    }

    /**
     * Order分页查询
     *
     * @param page
     * @param size
     */
    @Override
    public PageInfo<Order> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Order>(orderMapper.selectAll());
    }

    /**
     * Order条件查询
     *
     * @param order
     * @return
     */
    @Override
    public List<Order> findList(Order order) {
        //构建查询条件
        Example example = createExample(order);
        //根据构建的条件查询数据
        return orderMapper.selectByExample(example);
    }


    /**
     * Order构建查询对象
     *
     * @param order
     * @return
     */
    public Example createExample(Order order) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (order != null) {
            // 订单id
            if (!StringUtils.isEmpty(order.getId())) {
                criteria.andEqualTo("id", order.getId());
            }
            // 数量合计
            if (!StringUtils.isEmpty(order.getTotalNum())) {
                criteria.andEqualTo("totalNum", order.getTotalNum());
            }
            // 金额合计
            if (!StringUtils.isEmpty(order.getTotalMoney())) {
                criteria.andEqualTo("totalMoney", order.getTotalMoney());
            }
            // 优惠金额
            if (!StringUtils.isEmpty(order.getPreMoney())) {
                criteria.andEqualTo("preMoney", order.getPreMoney());
            }
            // 邮费
            if (!StringUtils.isEmpty(order.getPostFee())) {
                criteria.andEqualTo("postFee", order.getPostFee());
            }
            // 实付金额
            if (!StringUtils.isEmpty(order.getPayMoney())) {
                criteria.andEqualTo("payMoney", order.getPayMoney());
            }
            // 支付类型，1、在线支付、0 货到付款
            if (!StringUtils.isEmpty(order.getPayType())) {
                criteria.andEqualTo("payType", order.getPayType());
            }
            // 订单创建时间
            if (!StringUtils.isEmpty(order.getCreateTime())) {
                criteria.andEqualTo("createTime", order.getCreateTime());
            }
            // 订单更新时间
            if (!StringUtils.isEmpty(order.getUpdateTime())) {
                criteria.andEqualTo("updateTime", order.getUpdateTime());
            }
            // 付款时间
            if (!StringUtils.isEmpty(order.getPayTime())) {
                criteria.andEqualTo("payTime", order.getPayTime());
            }
            // 发货时间
            if (!StringUtils.isEmpty(order.getConsignTime())) {
                criteria.andEqualTo("consignTime", order.getConsignTime());
            }
            // 交易完成时间
            if (!StringUtils.isEmpty(order.getEndTime())) {
                criteria.andEqualTo("endTime", order.getEndTime());
            }
            // 交易关闭时间
            if (!StringUtils.isEmpty(order.getCloseTime())) {
                criteria.andEqualTo("closeTime", order.getCloseTime());
            }
            // 物流名称
            if (!StringUtils.isEmpty(order.getShippingName())) {
                criteria.andEqualTo("shippingName", order.getShippingName());
            }
            // 物流单号
            if (!StringUtils.isEmpty(order.getShippingCode())) {
                criteria.andEqualTo("shippingCode", order.getShippingCode());
            }
            // 用户名称
            if (!StringUtils.isEmpty(order.getUsername())) {
                criteria.andLike("username", "%" + order.getUsername() + "%");
            }
            // 买家留言
            if (!StringUtils.isEmpty(order.getBuyerMessage())) {
                criteria.andEqualTo("buyerMessage", order.getBuyerMessage());
            }
            // 是否评价
            if (!StringUtils.isEmpty(order.getBuyerRate())) {
                criteria.andEqualTo("buyerRate", order.getBuyerRate());
            }
            // 收货人
            if (!StringUtils.isEmpty(order.getReceiverContact())) {
                criteria.andEqualTo("receiverContact", order.getReceiverContact());
            }
            // 收货人手机
            if (!StringUtils.isEmpty(order.getReceiverMobile())) {
                criteria.andEqualTo("receiverMobile", order.getReceiverMobile());
            }
            // 收货人地址
            if (!StringUtils.isEmpty(order.getReceiverAddress())) {
                criteria.andEqualTo("receiverAddress", order.getReceiverAddress());
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (!StringUtils.isEmpty(order.getSourceType())) {
                criteria.andEqualTo("sourceType", order.getSourceType());
            }
            // 交易流水号
            if (!StringUtils.isEmpty(order.getTransactionId())) {
                criteria.andEqualTo("transactionId", order.getTransactionId());
            }
            // 订单状态,0:未完成,1:已完成，2：已退货
            if (!StringUtils.isEmpty(order.getOrderStatus())) {
                criteria.andEqualTo("orderStatus", order.getOrderStatus());
            }
            // 支付状态,0:未支付，1：已支付，2：支付失败
            if (!StringUtils.isEmpty(order.getPayStatus())) {
                criteria.andEqualTo("payStatus", order.getPayStatus());
            }
            // 发货状态,0:未发货，1：已发货，2：已收货
            if (!StringUtils.isEmpty(order.getConsignStatus())) {
                criteria.andEqualTo("consignStatus", order.getConsignStatus());
            }
            // 是否删除
            if (!StringUtils.isEmpty(order.getIsDelete())) {
                criteria.andEqualTo("isDelete", order.getIsDelete());
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
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Order
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 增加Order
     *
     * @param order
     */
    @Override
    public void add(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 根据ID查询Order
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Order全部数据
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }
}
