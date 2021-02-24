package com.changgou.goods.service.impl;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {


    @Autowired
    private BrandMapper brandMapper;

    /* 构建条件
    当名称不为空时，对名称进行模糊查找
    当首字母不为空时，对首字母进行查找
    */
    public Example createExample(Brand brand) {
        // 条件自定义搜索对象，需要传入字节码参数
        Example example = new Example(Brand.class);

        // 条件构造器
        Example.Criteria criteria = example.createCriteria();

        if (criteria != null) {
            if (!StringUtils.isEmpty(brand.getName())) {
                criteria.andLike("name", "%" + brand.getName() + "%");
            }
            if (!StringUtils.isEmpty(brand.getLetter())) {
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }
        return example;
    }

    @Override
    public List<Brand> findAll() {

        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {
        // 方法中带 selective ，会忽略空值
        // 比如说，只提供了 name 和 letter 属性
        // 拼接 SQL 语句:insert into tb_brand(name,latter) values(?,?)
        // 忽略了其他属性
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Brand brand) {
        // 条件自定义搜索对象，需要传入字节码参数
        Example example = new Example(Brand.class);

        // 条件构造器
        Example.Criteria criteria = example.createCriteria();

        if (criteria != null) {

            // 当名称不为空时，对名称进行模糊查找
            if (!StringUtils.isEmpty(brand.getName())) {
                criteria.andLike("name", "%" + brand.getName() + "%");
            }

            // 对首字母进行查找
            if (!StringUtils.isEmpty(brand.getLetter())) {
                criteria.andEqualTo("letter", brand.getLetter());
            }
        }

        return brandMapper.selectByExample(example);
    }

    /* 分页查询 */
    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {

        // 分页实现，传入 当前页、每页显示条数即可。
        PageHelper.startPage(page, size);

        // 后面必须查询集合，而不能查询单条记录。
        List<Brand> brands = brandMapper.selectAll();

        return new PageInfo<Brand>(brands);
    }


    /* 分页条件查询 */
    @Override
    public PageInfo<Brand> findPage(Brand brand, Integer page, Integer size) {
        // 分页
        PageHelper.startPage(page, size);

        // 搜索数据
        Example example = createExample(brand);
        List<Brand> brands = brandMapper.selectByExample(example);
        return new PageInfo<Brand>(brands);
    }
}
