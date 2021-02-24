package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Para;
import com.changgou.goods.service.ParaService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/para")
@CrossOrigin
public class ParaController {

    @Autowired
    private ParaService paraService;


    @PostMapping
    public Result add(@RequestBody Para para) {
        paraService.add(para);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable(value = "id") Integer id) {
        paraService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable(value = "id") Integer id, @RequestBody Para para) {
        para.setId(id);
        paraService.update(para);
        return new Result(true, StatusCode.OK, "更新成功");
    }

    @GetMapping
    public Result<List<Para>> findAll() {
        List<Para> paras = paraService.findAll();
        return new Result(true, StatusCode.OK, "查找成功", paras);
    }

    @GetMapping("/{id}")
    public Result<Para> findById(@PathVariable(value = "id") Integer id) {
        Para para = paraService.findById(id);
        return new Result(true, StatusCode.OK, "查找成功", para);
    }

    @PostMapping("/search")
    public Result<List<Para>> findList(@RequestBody Para para) {
        List<Para> paras = paraService.findList(para);
        return new Result(true, StatusCode.OK, "条件查找成功", para);
    }


    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Para>> findPage(@PathVariable(value = "page") Integer page,
                                           @PathVariable(value = "size") Integer size) {
        PageInfo<Para> pageInfo = paraService.findPage(page, size);
        return new Result(true, StatusCode.OK, "分页查找成功", pageInfo);
    }

    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Para>> findPage(@PathVariable(value = "page") Integer page,
                                           @PathVariable(value = "size") Integer size,
                                           @RequestBody Para para) {
        PageInfo<Para> pageInfo = paraService.findPage(page, size, para);
        return new Result(true, StatusCode.OK, "分页条件查找成功", pageInfo);
    }

    @GetMapping("/category/{id}")
    public Result<List<Para>> findByCateId(@PathVariable(value = "id") Integer id) {
        List paras = paraService.findByCateId(id);
        return new Result(true, StatusCode.OK, "查找成功", paras);
    }
}
