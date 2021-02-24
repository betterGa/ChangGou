package com.changgou.goods.service.impl;

import com.changgou.goods.dao.TemplateMapper;
import com.changgou.goods.pojo.Template;
import com.changgou.goods.service.TemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {
    @Autowired
    TemplateMapper templateMapper;

    @Override
    public void add(Template template) {
        templateMapper.insert(template);
    }

    @Override
    public void delete(Integer id) {
        templateMapper.deleteByPrimaryKey(id);

    }

    @Override
    public void update(Template template) {
        templateMapper.updateByPrimaryKey(template);
    }

    @Override
    public List<Template> findAll() {
        return templateMapper.selectAll();
    }

    @Override
    public Template findById(Integer id) {
        return (Template) templateMapper.selectByPrimaryKey(id);
    }

    /* 分页查询 */
    @Override
    public PageInfo<Template> findPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Template> templates = templateMapper.selectAll();
        return new PageInfo<>(templates);
    }

    /* 条件查询 */
    @Override
    public List<Template> findList(Template template) {
        Example example = createExample(template);
        return templateMapper.selectByExample(example);
    }

    /* 分页条件查询 */
    @Override
    public PageInfo<Template> findPage(Integer page, Integer size, Template template) {
        PageHelper.startPage(page, size);
        List<Template> templates = templateMapper.selectByExample(createExample(template));
        return new PageInfo<>(templates);
    }

    /* 封装构建条件的逻辑 */
    public Example createExample(Template template) {
        Example example = new Example(Template.class);
        Example.Criteria criteria = example.createCriteria();
        if(template!=null){
        if (StringUtil.isEmpty(template.getName()))
            criteria.andLike("name", "%" + template.getName() + "%");
        if (!StringUtils.isEmpty(template.getParaNum()))
            criteria.andEqualTo("paraNum");
        if (!StringUtils.isEmpty(template.getSpecNum()))
            criteria.andEqualTo("specNum");}
        return example;
    }
}
