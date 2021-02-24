package com.changgou.goods.service;

import com.changgou.goods.pojo.Para;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;

import java.util.List;

public interface ParaService {
    /* 增加参数信息 */
    public void add(Para para);

    /* 根据 ID 删除参数信息 */
    public void delete(Integer id);

    /* 根据 ID 更新参数信息 */
    public void update(Para para);

    /* 查找所有信息 */
    public List<Para> findAll();

    /* 根据 ID 查找信息 */
    public Para findById(Integer id);

    /* 条件查询 */
    public List<Para> findList(Para para);

    /* 分页查询 */
    public PageInfo<Para> findPage(Integer page,Integer size);

    /* 条件分页查询 */
    public PageInfo<Para> findPage(Integer page,Integer size,Para para);

    /* 根据 Category ID 查找 */
    public List<Para> findByCateId(Integer id);
}
