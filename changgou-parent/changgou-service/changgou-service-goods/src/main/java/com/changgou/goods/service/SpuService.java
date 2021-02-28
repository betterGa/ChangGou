package com.changgou.goods.service;

import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Spu;
import com.github.pagehelper.PageInfo;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口
 * @Date 2019/6/14 0:16
 *****/
public interface SpuService {

    /**
     * 批量下架
     * @param ids
     * @return
     */
    public int pullMany(Long[] ids);

    /**
     * 批量上架
     * @param ids
     */
    public int putMany(Long[] ids);

    /**
     * 商品上架
     * @param spuId
     */
    public void put(Long spuId);

    /**
     * 商品审核
     * @param spuId
     */
    public void audit(Long spuId);


    /**
     * 商品下架
     * @param spuId
     */
    public void pull(Long spuId);

    /**
     *  根据 spu ID 查询商品信息
     * @param id
     * @return
     */
    Goods findGoodsById(Long id);


    /**
     * 增加商品
     * @param goods
     */
    void saveGoods(Goods goods);

    /***
     * Spu多条件分页查询
     * @param spu
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(Spu spu, int page, int size);

    /***
     * Spu分页查询
     * @param page
     * @param size
     * @return
     */
    PageInfo<Spu> findPage(int page, int size);

    /***
     * Spu多条件搜索方法
     * @param spu
     * @return
     */
    List<Spu> findList(Spu spu);

    /***
     * 删除Spu
     * @param id
     */
    void delete(Long id);

    /***
     * 修改Spu数据
     * @param spu
     */
    void update(Spu spu);

    /***
     * 新增Spu
     * @param spu
     */
    void add(Spu spu);

    /**
     * 根据ID查询Spu
     * @param id
     * @return
     */
     Spu findById(Long id);

    /***
     * 查询所有Spu
     * @return
     */
    List<Spu> findAll();
}
