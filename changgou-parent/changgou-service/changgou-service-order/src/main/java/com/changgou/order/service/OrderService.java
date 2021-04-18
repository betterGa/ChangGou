package com.changgou.order.service;

import com.changgou.order.pojo.Order;
import com.github.pagehelper.PageInfo;

import java.text.ParseException;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Order业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface OrderService {

    /**
     * 修改订单状态
     *
     * @param outradeno
     * @param paytime
     * @param transcationid
     */
    void updateStatus(String outradeno, String paytime, String transcationid) throws ParseException;


    /**
     * 删除【逻辑删除，其实是修改订单状态】订单信息，回滚库存
     *
     * @param outradeno
     */
    void deleteOrder(String outradeno);

    /***
     * 添加订单实现
     * @param order
     */
    void addOrder(Order order);


    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(Order order, int page, int size);

    /***
     * Order分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(int page, int size);

    /***
     * Order多条件搜索方法
     * @param order
     * @return
     */
    List<Order> findList(Order order);

    /***
     * 删除Order
     * @param id
     */
    void delete(String id);

    /***
     * 修改Order数据
     * @param order
     */
    void update(Order order);

    /***
     * 新增Order
     * @param order
     */
    void add(Order order);

    /**
     * 根据ID查询Order
     *
     * @param id
     * @return
     */
    Order findById(String id);

    /***
     * 查询所有Order
     * @return
     */
    List<Order> findAll();
}
