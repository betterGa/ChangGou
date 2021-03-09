package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SkuService;
import entity.Result;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

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


        // 如果用户在前端页面中已经输入了 比如说 分类、品牌 的条件
        // 那么就没必要再根据 分类、品牌进行分组查询了，只需要根据条件进行搜索。

        // 如果用户没有输入条件 或者 没有输入分类条件
        if (searchMap == null || searchMap.get("category") == null) {
            // 根据分类进行分组查询
            List<String> categoryList = searchCategoryList(builder);
            resultMap.put("category", categoryList);
        }

        // 如果用户没有输入品牌 或者 没有输入品牌条件
        if (searchMap == null || searchMap.get("brand") == null) {
            // 根据品牌名称进行分组查询
            List<String> brandList = searchBrandList(builder);
            resultMap.put("brand", brandList);
        }


        // 根据规格进行分组查询
        Map<String, Set<String>> specList = searchSpecList(builder);


        resultMap.put("spec", specList);

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

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (searchMap != null && searchMap.size() > 0) {
            // 根据关键词搜索
            String keyWords = searchMap.get("keywords");

            String category = searchMap.get("category");
            String brand = searchMap.get("brand");

            if (!StringUtils.isEmpty(keyWords)) {
                //builder.withQuery(
                //QueryBuilders.queryStringQuery(keyWords).field("name"));

                // 如果关键词不为空，根据关键词在 name 域进行搜索
                boolQueryBuilder.must(QueryBuilders.queryStringQuery(keyWords).field("name"));
            }

            // 分类过滤
            // 如果分类不为空
            if (!StringUtils.isEmpty(category)) {
                // 不需要分词
                boolQueryBuilder.must(QueryBuilders.termQuery("categoryName", "category"));
            }

            // 品牌过滤
            // 如果品牌不为空
            if (!StringUtils.isEmpty(brand)) {
                // 不需要分词
                boolQueryBuilder.must(QueryBuilders.termQuery("brandName", "brand"));
            }

            // 规格过滤
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                // 如果 key 前缀是 spec_，说明需要根据规格过滤
                if (key.startsWith("spec_")) {
                    String value = entry.getValue();

                    // 不需要分词
                    // 比如传入 spec_像素，在索引库里，域是 specMap.像素
                    boolQueryBuilder.must(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", value));
                }
            }
        }

        // 价格区间查询
        // 前端写s的是 price 0-500元 500-1000元 1000-1500元 1500-2000元 2000-3000元 3000元以上

        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {

            // 需要去掉 "元" 和 “以上” 变成 0-500 500-1000 1000-1500 1500-2000 2000-3000 3000
            price = price.replace("元", "").replace("以上", "");

            // 需要根据 “-” 进行分割 [0,500] [500,1000] ... ... [3000]
            String[] prices = price.split("-");

            if (prices != null && prices.length > 0) {

                // price[0]!=null price>price[0]
                if (prices.length == 1) {
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                } else {

                    // price[1]!=null price<=price[1]
                    boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));
                }
            }
        }
        // 实现分页，如果用户没有传入分页参数，默认 第 1 页
        Integer pageNum = coverterPage(searchMap);
        // 默认每页显示 3 条数据
        Integer size = 3;
        builder.withPageable(PageRequest.of(pageNum - 1, size));

        // 将 BoolQueryBuilder 对象填充给 NativeSearchQueryBuilder
        builder.withQuery(boolQueryBuilder);
        return builder;
}

    /**
     * 接受前端分页参数 页码、每页数据条数
     *
     * @param searchMap
     * @return
     */
    public Integer coverterPage(Map<String, String> searchMap) {
        if (searchMap != null) {
            String pageNum = searchMap.get("pageNum");
            try {
                Integer pageNum1 = Integer.parseInt(pageNum);
                if (pageNum1 >= 1) {
                    return pageNum1;
                }
            }catch (NumberFormatException numberFormatException){

            }
        }
        return 1;
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
     * 抽取根据分类进行分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public List<String> searchCategoryList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {
        // 分组查询分类集合
        // addAggregation 添加聚合操作
        // 第一个参数需要传入分组的依据，即 根据哪个域进行分组
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategory").field("categoryName"));

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(),
                SkuInfo.class);

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
     * 根据品牌名称分组查询
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

    /**
     * 根据规格进行分组查询
     *
     * @param nativeSearchQueryBuilder
     * @return
     */
    public Map<String, Set<String>> searchSpecList(NativeSearchQueryBuilder nativeSearchQueryBuilder) {

        // 使用 spec.keyword 表示不分词
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpec").field("spec.keyword").
                size(10000));

        AggregatedPage<SkuInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(),
                SkuInfo.class);

        // 按照规格进行分类查询
        // 其实相当于对 spec 进行去重，可以提高后续对它的 value 去重的效率
        StringTerms stringTerms = aggregatedPage.getAggregations().get("skuSpec");

        List<String> specList = new ArrayList<String>();

        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String specName = bucket.getKeyAsString();
            specList.add(specName);
        }
        Map<String, Set<String>> allSpec = putAllSpec(specList);
        return allSpec;
    }


    /**
     * 规格数据操作
     *
     * @param specList
     * @return
     */
    private Map<String, Set<String>> putAllSpec(List<String> specList) {
        Map<String, Set<String>> allSpec = new HashMap<String, Set<String>>();

        // 遍历 specList
        for (String spec : specList) {
            // 将 List 先转化成 Map
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);

            // 再将 Map 转化成 Map<String,Set<String>>,key 不变，需要把 value 添加到 Set 中
            for (Map.Entry<String, String> entry : specMap.entrySet()) {
                String entryKey = entry.getKey();
                if (allSpec.get(entryKey) == null) {
                    Set set = new HashSet<>();
                    set.add(entry.getValue());
                    allSpec.put(entryKey, set);
                } else {
                    allSpec.get(entryKey).add(entry.getValue());
                }
            }
        }
        return allSpec;
    }
}

