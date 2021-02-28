package com.changgou.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.dao.SkuMapper;
import com.changgou.goods.dao.SpuMapper;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.pojo.Goods;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.goods.service.SpuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/****
 * @Author:shenkunlin
 * @Description:Spu业务层接口实现类
 * @Date 2019/6/14 0:16
 *****/
@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public int pullMany(Long[] ids) {
        // 未删除 已上架的才能下架
        // update tb_sku set IsMarkable=0 where id in(ids) and isdelete=0 and status=1
        Example example=new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        criteria.andEqualTo("isMarketable","1");
        criteria.andEqualTo("isDelete",0);

        Spu spu=new Spu();
        // 下架
        spu.setIsMarketable("0");
        return spuMapper.updateByExampleSelective(spu,example);
    }

    @Override
    public int putMany(Long[] ids) {
        // 未删除 已审核的才能上架
        // update tb_sku set IsMarkable=1 where id in(ids) and isdelete=0 and status=1
        Example example=new Example(Spu.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));
        criteria.andEqualTo("isDelete",0);
        criteria.andEqualTo("status","1");

        Spu spu=new Spu();
        // 上架
        spu.setIsMarketable("1");
        return spuMapper.updateByExampleSelective(spu,example);
    }

    @Override
    public void put(Long spuId) {
        Spu spu=spuMapper.selectByPrimaryKey(spuId);

        // 检查是否删除
        if(spu.getIsDelete().equals("1")){
            throw new RuntimeException("此商品已删除！");
        }

        // 检查是否进行了审核
        if(!spu.getStatus().equals(1)){
            throw new RuntimeException("未通过审核的商品不能上架！");
        }

        // 上架状态
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 商品审核
     * @param spuId
     */
    @Override
    public void audit(Long spuId) {
        // 先查询商品
        Spu spu=spuMapper.selectByPrimaryKey(spuId);

        // 判断商品是否被删除
        if(spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("不能对已删除的商品进行审核");
        }

        // 审核通过，并自动上架
        spu.setStatus("1");
        spu.setIsMarketable("1");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    @Override
    public void pull(Long spuId) {
        // 先查询商品
        Spu spu=spuMapper.selectByPrimaryKey(spuId);

        // 判断商品是否被删除
        if(spu.getIsDelete().equalsIgnoreCase("1")){
            throw new RuntimeException("此商品已删除");
        }

        spu.setIsMarketable("0");
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 根据 spu ID 查询商品信息
     *
     * @param id
     * @return
     */

    @Override
    public Goods findGoodsById(Long id) {
        // 查询 spu
        Spu spu = spuMapper.selectByPrimaryKey(id);

        // 查询 sku
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skuList = skuMapper.select(sku);

        Goods goods = new Goods(spu, skuList);
        return goods;
    }

    /**
     * 添加商品信息
     *
     * @param goods
     */
    @Override
    public void saveGoods(Goods goods) {
        Spu spu = goods.getSpu();

        // 修改商品需要先删除原先全部的 sku，再进行添加
        // 区分是直接添加商品，还是因为修改商品才进行添加
        // 根据 spu ID 是否为空
        if (spu.getId() == null) {

            // 直接添加商品
            spu.setId(idWorker.nextId());

            // 商品一发布就上架
            spu.setIsMarketable("1");

            spuMapper.insertSelective(spu);
        }else{
            // 修改商品
            spuMapper.updateByPrimaryKey(spu);

            // 将原先 spu 对应的 sku 全部删除
            Sku sku=new Sku();
            sku.setSpuId(spu.getId());
            spuMapper.delete(spu);
        }

        // Sku 集合
        List<Sku> skuList = goods.getSkuList();
        for (Sku sku : skuList) {
            sku.setId(idWorker.nextId());

            // name 由 spu name、规格组成
            String spuName = spu.getName();

            // 在数据库里规格字段：{"电视音响效果":"立体声","电视屏幕尺寸":"20英寸","尺码":"165"}
            // 是 json 形式，需要把它转换成 map

            // 防止空指针
            if (StringUtils.isEmpty(sku.getSpec())) {
                sku.setSpec("{}");
            }
            Map<String, String> specMap = JSON.parseObject(sku.getSpec(), Map.class);
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                spuName += "" + entry.getValue();
            }

            Date date = new Date();
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setSpuId(spu.getId());

            // 三级分类
            sku.setCategoryId(spu.getCategory3Id());

            // 品牌名称是要通过查询才能得到的，所以需要先查询品牌信息
            Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
            sku.setBrandName(brand.getName());

            // 将 sku 添加到数据库中
            skuMapper.insertSelective(sku);
        }
    }

    /**
     * Spu条件+分页查询
     *
     * @param spu  查询条件
     * @param page 页码
     * @param size 页大小
     * @return 分页结果
     */
    @Override
    public PageInfo<Spu> findPage(Spu spu, int page, int size) {
        //分页
        PageHelper.startPage(page, size);
        //搜索条件构建
        Example example = createExample(spu);
        //执行搜索
        return new PageInfo<Spu>(spuMapper.selectByExample(example));
    }

    /**
     * Spu分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageInfo<Spu> findPage(int page, int size) {
        //静态分页
        PageHelper.startPage(page, size);
        //分页查询
        return new PageInfo<Spu>(spuMapper.selectAll());
    }

    /**
     * Spu条件查询
     *
     * @param spu
     * @return
     */
    @Override
    public List<Spu> findList(Spu spu) {
        //构建查询条件
        Example example = createExample(spu);
        //根据构建的条件查询数据
        return spuMapper.selectByExample(example);
    }


    /**
     * Spu构建查询对象
     *
     * @param spu
     * @return
     */
    public Example createExample(Spu spu) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (spu != null) {
            // 主键
            if (!StringUtils.isEmpty(spu.getId())) {
                criteria.andEqualTo("id", spu.getId());
            }
            // 货号
            if (!StringUtils.isEmpty(spu.getSn())) {
                criteria.andEqualTo("sn", spu.getSn());
            }
            // SPU名
            if (!StringUtils.isEmpty(spu.getName())) {
                criteria.andLike("name", "%" + spu.getName() + "%");
            }
            // 副标题
            if (!StringUtils.isEmpty(spu.getCaption())) {
                criteria.andEqualTo("caption", spu.getCaption());
            }
            // 品牌ID
            if (!StringUtils.isEmpty(spu.getBrandId())) {
                criteria.andEqualTo("brandId", spu.getBrandId());
            }
            // 一级分类
            if (!StringUtils.isEmpty(spu.getCategory1Id())) {
                criteria.andEqualTo("category1Id", spu.getCategory1Id());
            }
            // 二级分类
            if (!StringUtils.isEmpty(spu.getCategory2Id())) {
                criteria.andEqualTo("category2Id", spu.getCategory2Id());
            }
            // 三级分类
            if (!StringUtils.isEmpty(spu.getCategory3Id())) {
                criteria.andEqualTo("category3Id", spu.getCategory3Id());
            }
            // 模板ID
            if (!StringUtils.isEmpty(spu.getTemplateId())) {
                criteria.andEqualTo("templateId", spu.getTemplateId());
            }
            // 运费模板id
            if (!StringUtils.isEmpty(spu.getFreightId())) {
                criteria.andEqualTo("freightId", spu.getFreightId());
            }
            // 图片
            if (!StringUtils.isEmpty(spu.getImage())) {
                criteria.andEqualTo("image", spu.getImage());
            }
            // 图片列表
            if (!StringUtils.isEmpty(spu.getImages())) {
                criteria.andEqualTo("images", spu.getImages());
            }
            // 售后服务
            if (!StringUtils.isEmpty(spu.getSaleService())) {
                criteria.andEqualTo("saleService", spu.getSaleService());
            }
            // 介绍
            if (!StringUtils.isEmpty(spu.getIntroduction())) {
                criteria.andEqualTo("introduction", spu.getIntroduction());
            }
            // 规格列表
            if (!StringUtils.isEmpty(spu.getSpecItems())) {
                criteria.andEqualTo("specItems", spu.getSpecItems());
            }
            // 参数列表
            if (!StringUtils.isEmpty(spu.getParaItems())) {
                criteria.andEqualTo("paraItems", spu.getParaItems());
            }
            // 销量
            if (!StringUtils.isEmpty(spu.getSaleNum())) {
                criteria.andEqualTo("saleNum", spu.getSaleNum());
            }
            // 评论数
            if (!StringUtils.isEmpty(spu.getCommentNum())) {
                criteria.andEqualTo("commentNum", spu.getCommentNum());
            }
            // 是否上架,0已下架，1已上架
            if (!StringUtils.isEmpty(spu.getIsMarketable())) {
                criteria.andEqualTo("isMarketable", spu.getIsMarketable());
            }
            // 是否启用规格
            if (!StringUtils.isEmpty(spu.getIsEnableSpec())) {
                criteria.andEqualTo("isEnableSpec", spu.getIsEnableSpec());
            }
            // 是否删除,0:未删除，1：已删除
            if (!StringUtils.isEmpty(spu.getIsDelete())) {
                criteria.andEqualTo("isDelete", spu.getIsDelete());
            }
            // 审核状态，0：未审核，1：已审核，2：审核不通过
            if (!StringUtils.isEmpty(spu.getStatus())) {
                criteria.andEqualTo("status", spu.getStatus());
            }
        }
        return example;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        spuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改Spu
     *
     * @param spu
     */
    @Override
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKey(spu);
    }

    /**
     * 增加Spu
     *
     * @param spu
     */
    @Override
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 根据ID查询Spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu findById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 查询Spu全部数据
     *
     * @return
     */
    @Override
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }
}
