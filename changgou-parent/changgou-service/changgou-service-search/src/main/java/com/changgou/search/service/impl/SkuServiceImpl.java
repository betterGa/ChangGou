package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    SkuEsMapper skuEsMapper;

    @Autowired
    SkuFeign skuFeign;

    /**
     * 可以实现索引库的增删改查，适用于高级搜索
     */
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 导入索引库
     */

    @Override
    public void importData() {

        // Feign 调用，查询 List<Sku>
        Result<List<Sku>> skus = skuFeign.findAll();

        // 将 List<Sku> 转化成 List<SkuInfo>
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skus.getData()), SkuInfo.class);

        // 遍历当前 skuInfos，获取列表规格列表
        for (SkuInfo skuInfo : skuInfos) {
            Map<String, Object> specMap = JSON.parseObject(skuInfo.getSpec(), Map.class);

            // 将域存入 Map<String, Object>，key 就会生成动态域，
            // 域的名字为 key,值为 value
            skuInfo.setSpecMap(specMap);
        }


        // 调用 dao 实现数据批量导入
        skuEsMapper.saveAll(skuInfos);
    }


    /**
     * 多条件搜索
     *
     * @param searchMap
     * @return
     */
    /*
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        // 构建搜索条件对象
        NativeSearchQueryBuilder builder =builderBasicQuery(searchMap);


        //
        if (searchMap != null && searchMap.size() > 0) {

            // 根据关键词搜索
            String keyWords = searchMap.get("keywords");

            if (!StringUtils.isEmpty(keyWords)) {
                builder.withQuery(
                        QueryBuilders.queryStringQuery(keyWords).field("name"));
            }

        }

        // 第二个参数需要传入 搜索的结果类型（页面展示的是集合数据）
        // AggregatedPage<SkuInfo> 是对结果集的封装
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(
                builder.build(), SkuInfo.class);

        // 根据分类进行分组查询
        List<String> categoryList = searchCategoryList(builder);

        // 根据品牌名称进行分组查询
        List<String> brandList = searchBrandList(builder);


        // 获取数据结果集
        List<SkuInfo> contents = page.getContent();

        // 获取总记录数
        long totalNums = page.getTotalElements();

        // 获取总页数
        int totalPages = page.getTotalPages();

        // 封装 Map 存储数据作为结果
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("rows", contents);
        resultMap.put("totalNums", totalNums);
        resultMap.put("totalPages", totalPages);
        resultMap.put("category", categoryList);
        return resultMap;
    }
*/

    /**
     * 多条件搜索
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        // 构建搜索条件对象
        NativeSearchQueryBuilder builder = builderBasicQuery(searchMap);


        // 集合搜索
        Map<String, Object> resultMap = searchlist(builder);

        // 根据分类进行分组查询
        List<String> categoryList = searchCategoryList(builder);

        // 根据品牌名称进行分组查询
        List<String> brandList = searchBrandList(builder);

        resultMap.put("category", categoryList);
        resultMap.put("brand", brandList);

        return resultMap;
    }


    /**
     * 搜索条件封装
     *
     * @param searchMap
     * @return
     */
    public NativeSearchQueryBuilder builderBasicQuery(Map<String, String> searchMap) {
        // 构建搜索条件对象
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        if (searchMap != null && searchMap.size() > 0) {

            // 根据关键词搜索
            String keyWords = searchMap.get("keywords");

            if (!StringUtils.isEmpty(keyWords)) {
                builder.withQuery(
                        QueryBuilders.queryStringQuery(keyWords).field("name"));
            }

        }
        return builder;
    }


    /**
     * 集合搜索封装
     *
     * @param builder
     * @return
     */
    public Map<String, Object> searchlist(NativeSearchQueryBuilder builder) {

        // 第二个参数需要传入 搜索的结果类型（页面展示的是集合数据）
        // AggregatedPage<SkuInfo> 是对结果集的封装
        AggregatedPage<SkuInfo> page = elasticsearchTemplate.queryForPage(
                builder.build(), SkuInfo.class);

        // 获取数据结果集
        List<SkuInfo> contents = page.getContent();

        // 获取总记录数
        long totalNums = page.getTotalElements();

        // 获取总页数
        int totalPages = page.getTotalPages();

        // 封装 Map 存储数据作为结果
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("rows", contents);
        resultMap.put("totalNums", totalNums);
        resultMap.put("totalPages", totalPages);
        return resultMap;
    }

    /**
     * 抽取根据分类进行分组的查询逻辑
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 分组查询分类集合
        // addAggregation 添加聚合操作
        // 第一个参数需要传入分组的依据，即 根据哪个域进行分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        // 可以根据多个域进行分组。
        // 获取指定域的集合数据 {手机，电脑，电视}
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuCategory");

        List<String> categoryList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            // 获取其中一个分类名称，比如 手机 或者 电脑 或者 电视
            String categoryName = bucket.getKeyAsString();
            categoryList.add(categoryName);
        }
        return categoryList;
    }

    /**
     * 根据品牌名称分组查询逻辑
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public List<String> searchBrandList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 分组查询品牌集合
        // addAggregation 添加聚合操作
        // 第一个参数需要传入分组的依据，即 根据哪个域进行分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrand").field("brandName"));

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), SkuInfo.class);

        // 可以根据多个域进行分组。
        // 获取指定域的集合数据 {TCL，海尔，华为}
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuBrand");

        List<String> brandList = new ArrayList<String>();
        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            // 获取其中一个分类名称，比如 TCL 或者 海尔 或者 华为
            String categoryName = bucket.getKeyAsString();
            brandList.add(categoryName);
        }
        return brandList;
    }
}


