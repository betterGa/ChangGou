package com.changgou.order.feign;
import com.changgou.order.pojo.ReturnCause;
import com.github.pagehelper.PageInfo;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/18 13:58
 *****/
@FeignClient(name="order")
@RequestMapping("/returnCause")
public interface ReturnCauseFeign {

    /***
     * ReturnCause分页条件搜索实现
     * @param returnCause
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@RequestBody(required = false) ReturnCause returnCause, @PathVariable  int page, @PathVariable  int size);

    /***
     * ReturnCause分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    Result<PageInfo> findPage(@PathVariable int page, @PathVariable  int size);

    /***
     * 多条件搜索品牌数据
     * @param returnCause
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<ReturnCause>> findList(@RequestBody(required = false) ReturnCause returnCause);

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    Result delete(@PathVariable Integer id);

    /***
     * 修改ReturnCause数据
     * @param returnCause
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    Result update(@RequestBody ReturnCause returnCause,@PathVariable Integer id);

    /***
     * 新增ReturnCause数据
     * @param returnCause
     * @return
     */
    @PostMapping
    Result add(@RequestBody ReturnCause returnCause);

    /***
     * 根据ID查询ReturnCause数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<ReturnCause> findById(@PathVariable Integer id);

    /***
     * 查询ReturnCause全部数据
     * @return
     */
    @GetMapping
    Result<List<ReturnCause>> findAll();
}