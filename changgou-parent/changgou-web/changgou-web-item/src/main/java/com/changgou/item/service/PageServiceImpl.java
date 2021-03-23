package com.changgou.item.service;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageServiceImpl implements PageService{

    @Autowired
    SpuFeign spuFeign;

    @Autowired
    CategoryFeign categoryFeign;

    @Autowired
    SkuFeign skuFeign;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${pagepath}")
    private String pagepath;

    private Map<String, Object> buildDataModel(Long spuId) {
        //构建数据模型
        Map<String, Object> dataMap = new HashMap<>(16);
        //获取spu 和SKU列表
        Result<Spu> result = spuFeign.findById(spuId);
        Spu spu = result.getData();

        dataMap.put("spu", spu);

        //获取分类信息
        dataMap.put("category1", categoryFeign.findById(spu.getCategory1Id()).getData());
        dataMap.put("category2", categoryFeign.findById(spu.getCategory2Id()).getData());
        dataMap.put("category3", categoryFeign.findById(spu.getCategory3Id()).getData());
        if (spu.getImages() != null) {
            dataMap.put("imageList", spu.getImages().split(","));
        }

        dataMap.put("specificationList", JSON.parseObject(spu.getSpecItems(), Map.class));


        //根据spuId查询Sku集合
        Sku skuCondition = new Sku();
        skuCondition.setSpuId(spu.getId());
        Result<List<Sku>> resultSku = skuFeign.findList(skuCondition);
        dataMap.put("skuList", resultSku.getData());
        return dataMap;
    }

    /**
     * 生成静态页
     * @param spuId
     */
    @Override
    public void createPageHtml(Long spuId) {

        // 上下文
        Context context=new Context();
        Map<String, Object> dataModel=buildDataModel(spuId);
        context.setVariables(dataModel);

        // 准备文件
        File dir=new File(pagepath);
        if(!dir.exists()){
            dir.mkdir();
        }

        File dest=new File(dir,spuId+".html");

        // 生成页面
        try(PrintWriter writer=new PrintWriter(dest,"UTF-8")) {
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
