package com.changgou.goods.service;

import com.changgou.goods.pojo.Spec;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;

import java.util.List;


public interface SpecService{
    /* 增加规模信息 */
    public void add(Spec spec);

    /* 删除规模信息 */
    public void delete(Integer id);

    /* 更新规模信息 */
    public void update(Spec spec);

    /* 查询所有信息 */
    public List<Spec> findAll();

    /* 按 ID 查询 */
    public Spec findById(Integer id);

    /* 根据 template_ID 查询 */
    public List<Spec> findByTempId(Integer id);

    /* 根据 category_ID 查询 */
    public List<Spec> findByCateGory(Integer id);

    /* 条件查询 */
    public List<Spec> findList(Spec spec);

    /* 分页查询 */
    public PageInfo<Spec> findPage(Integer page,Integer size);

    /* 分页条件查询 */
    public PageInfo<Spec> findPage(Integer page,Integer size,Spec spec);

}
