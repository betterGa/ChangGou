package com.changgou.goods.feign;

import com.changgou.goods.pojo.Brand;
import entity.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "goods")
@RequestMapping("/brand")
public interface BrandFeign {

    // 根据商家名称查询品牌列表
    @GetMapping(value = "/findByStore/{storename}")
    public Result<List<Brand>> findByStore(@PathVariable(value = "storename")String storename);

}
