package com.changgou.goods.service;

import com.changgou.goods.pojo.Brand;
import com.github.pagehelper.PageInfo;
import org.springframework.context.annotation.Primary;

import java.util.List;

public interface BrandService {

    /* 查询所有品牌 */
    List<Brand> findAll();

    /* 根据 ID 查询 */
    Brand findById(Integer id);

    /* 增加品牌 */
    void add(Brand brand);

    /* 根据 ID 修改 */
    void update(Brand brand);

    /* 根据 ID 删除 */
    void delete(Integer id);

    /* 根据品牌信息多条件搜索 */
    List<Brand> findList(Brand brand);

    /* 分页搜索
    * @param page: 当前页
    * @param size: 每页显示的条数
    */
    PageInfo<Brand> findPage(Integer page,Integer size);


    /* 分页条件搜索 */
    PageInfo<Brand> findPage(Brand brand,Integer page,Integer size);
}
