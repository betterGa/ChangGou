package com.changgou.search.controller;

import com.changgou.search.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuService skuService;

    @GetMapping("/import")
    public Result search(){
       skuService.importData();
       return new Result(true, StatusCode.OK,"导入数据到索引库成功！");
    }

    /**
     * 搜索
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map<String,String> searchMap) throws Exception{
        return  skuService.search(searchMap);
    }
}
