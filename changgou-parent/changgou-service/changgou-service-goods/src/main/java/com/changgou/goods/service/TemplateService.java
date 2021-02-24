package com.changgou.goods.service;

import com.changgou.goods.pojo.Template;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface TemplateService {
    /* 添加模板信息 */
    public void add(Template template);

    /* 删除模板信息 */
    public void delete(Integer id);

    /* 修改模板信息 */
    public void update(Template template);

    /* 查找所有模板信息 */
    public List<Template> findAll();

    /* 根据 ID 查找模板信息 */
    public Template findById(Integer id);

    /* 分页查询 */
    public PageInfo<Template> findPage(Integer page,Integer size);

    /* 条件查询 */
    public List<Template> findList(Template template);

    /* 分页条件查询 */
    public PageInfo<Template> findPage(Integer page,Integer size,Template template);

}
