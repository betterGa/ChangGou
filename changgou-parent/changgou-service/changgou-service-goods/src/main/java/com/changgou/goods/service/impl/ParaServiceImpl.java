package com.changgou.goods.service.impl;

import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.ParaMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Para;
import com.changgou.goods.service.ParaService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ParaServiceImpl implements ParaService {
    @Autowired
    ParaMapper paraMapper;

    @Autowired
    CategoryMapper categoryMapper;


    /* 构建条件查询 */
    public Example createExample(Para para) {
        Example example = new Example(Para.class);
        Example.Criteria criteria = example.createCriteria();
        if (para != null) {
            if (!StringUtils.isEmpty(para.getName()))
                criteria.andLike("name", "%" + para.getName() + "%");
            if (!StringUtils.isEmpty(para.getOptions()))
                criteria.andLike("options", "%" + para.getOptions() + "%");
            if (!StringUtils.isEmpty(para.getSeq()))
                criteria.andEqualTo("seq", para.getSeq());
            if (!StringUtils.isEmpty(para.getTemplateId()))
                criteria.andEqualTo("templateId", para.getTemplateId());
        }
        return example;
    }


    @Override
    public void add(Para para) {
        paraMapper.insert(para);
    }

    @Override
    public void delete(Integer id) {
        paraMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Para para) {
        paraMapper.updateByPrimaryKey(para);
    }

    @Override
    public List<Para> findAll() {
        return paraMapper.selectAll();
    }

    @Override
    public Para findById(Integer id) {
        return paraMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Para> findList(Para para) {
        Example example = createExample(para);
        return paraMapper.selectByExample(example);
    }

    @Override
    public PageInfo<Para> findPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Para> paras = paraMapper.selectAll();
        return new PageInfo<>(paras);
    }

    @Override
    public PageInfo<Para> findPage(Integer page, Integer size, Para para) {
        PageHelper.startPage(page, size);
        List<Para> paras = paraMapper.selectByExample(createExample(para));
        return new PageInfo<>(paras);
    }

    @Override
    public List<Para> findByCateId(Integer id) {
        // 先根据 Category ID 查出 template_id
        Category category = categoryMapper.selectByPrimaryKey(id);
        Para para=new Para();
        para.setTemplateId(category.getTemplateId());
        return paraMapper.select(para);
    }
}
