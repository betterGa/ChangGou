package com.changgou.goods.service.impl;

import com.changgou.goods.dao.CategoryMapper;
import com.changgou.goods.dao.SpecMapper;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class SpecServiceImpl implements SpecService {
    @Autowired
    SpecMapper specMapper;

    @Autowired
    CategoryMapper categoryMapper;

    // 构建条件
    public static Example createExample(Spec spec) {
        Example example = new Example(Spec.class);
        Example.Criteria criteria = example.createCriteria();
        if (spec != null) {
            if (!StringUtils.isEmpty(spec.getName()))
                criteria.andLike("name", "%" + spec.getName() + "%");
            if (!StringUtils.isEmpty(spec.getId()))
                criteria.andEqualTo("id", spec.getId());
            if (!StringUtils.isEmpty(spec.getOptions()))
                criteria.andLike("options", "%" + spec.getOptions() + "%");
            if (!StringUtils.isEmpty(spec.getSeq()))
                criteria.andEqualTo("seq", spec.getSeq());
            if (!StringUtils.isEmpty(spec.getTemplateId()))
                criteria.andEqualTo("templateId", spec.getTemplateId());
        }
        return example;
    }

    @Override
    public void add(Spec spec) {
        specMapper.insert(spec);
    }

    @Override
    public void delete(Integer id) {
        specMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Spec spec) {

        specMapper.updateByPrimaryKey(spec);
    }

    @Override
    public List<Spec> findAll() {
        return specMapper.selectAll();
    }

    @Override
    public Spec findById(Integer id) {
        return specMapper.selectByPrimaryKey(id);
    }

    /* 根据 Template Id 查询 */
    @Override
    public List<Spec> findByTempId(Integer id) {
        Example example = new Example(Spec.class);
        Example.Criteria criteria = example.createCriteria();
        // select * from tb_spec,tb_template where template_id=tb_template.id;
        // 只需要在 tb_spec 表中查即可......
        criteria.andEqualTo("templateId", id);
        return specMapper.selectByExample(example);
    }

    @Override
    public List<Spec> findByCateGory(Integer id) {

        /*
        Example example=new Example(Spec.class);
        Example.Criteria criteria=example.createCriteria();
        // select * from tb_spec,tb_category where template_id=
        // (select template_id from tb_category where templated_id=id;)

        // 先通过 id 查到 category 表里的记录，因为是 id 所有只会有一个对象
        Category category= categoryMapper.selectByPrimaryKey(id);

        // 通过对象的 templated_id 属性查询
        criteria.andEqualTo("templateId",category.getTemplateId());

        return specMapper.selectByExample(example);
*/

        // 考虑不用 example
        Category category=categoryMapper.selectByPrimaryKey(id);
        Spec spec=new Spec();
        spec.setTemplateId(category.getTemplateId());
        return specMapper.select(spec);

    }

    /* 条件查询 */
    @Override
    public List<Spec> findList(Spec spec) {
        Example example = createExample(spec);
        return specMapper.selectByExample(example);
    }

    /* 分页查询 */
    @Override
    public PageInfo<Spec> findPage(Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Spec> specs = specMapper.selectAll();
        return new PageInfo<>(specs);
    }

    /* 分页条件查询 */
    @Override
    public PageInfo<Spec> findPage(Integer page, Integer size, Spec spec) {
        PageHelper.startPage(page, size);
        List<Spec> specs = specMapper.selectByExample(createExample(spec));
        return new PageInfo<>(specs);
    }
}
