package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;

import com.changgou.order.pojo.OrderItem;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.goods.feign *
 * @since 1.0
 */
@FeignClient(value="goods")
@RequestMapping("/sku")
public interface SkuFeign {
    /**
     * 查询 sku 全部数据
     * @return
     */
    @GetMapping("/findAll")
    Result<List<Sku>> findAll();

    /**
     * 查询符合条件的状态的 SKU 的列表
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    public Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);

    /**
     * 根据条件搜索的 SKU 的列表
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);


    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name="id") Long id);


    @GetMapping(value = "/decr")
    public Result decrCount(@RequestParam Map<String,String> decrMap);

    @GetMapping(value = "/asc")
    public Result ascCount(@RequestParam Map<String,String> ascMap);

}
