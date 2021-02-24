package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Spec;
import com.changgou.goods.service.SpecService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
@CrossOrigin
public class SpecController {
    @Autowired
    SpecService specService;

    @PostMapping
    public Result add(@RequestBody Spec spec) {
        specService.add(spec);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable(value = "id") Integer id) {
        specService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable(value = "id") Integer id, @RequestBody Spec spec) {
        spec.setId(id);
        specService.update(spec);
        return new Result(true, StatusCode.OK, "更新成功");
    }

    @GetMapping
    public Result<List<Spec>> findAll() {
        List<Spec> specs = specService.findAll();
        return new Result<>(true, StatusCode.OK, "查找成功", specs);
    }

    @GetMapping("/{id}")
    public Result<Spec> findById(@PathVariable(value = "id") Integer id) {
        Spec spec = specService.findById(id);
        return new Result<>(true, StatusCode.OK, "查询成功", spec);
    }

    /* 根据 Template Id 查询 */
    @GetMapping("/searchByTemId/{id}")
    public Result<Spec> findByTempId(@PathVariable(value = "id") Integer id) {
        List<Spec> specs = specService.findByTempId(id);
        return new Result<>(true, StatusCode.OK, "查询成功", specs);
    }

    /* 根据 category 类别表 id 查询 */
    @GetMapping("/category/{id}")
    public Result<List<Spec>> findByCateId(@PathVariable Integer id) {
        List<Spec> specs = specService.findByCateGory(id);
        return new Result<>(true, StatusCode.OK, "查询成功", specs);
    }

    /* 条件查询 */
    @PostMapping("/search")
    public Result<List<Spec>> findList(@RequestBody Spec spec) {
        List<Spec> specs = specService.findList(spec);
        return new Result<>(true, StatusCode.OK, "条件查询成功", specs);
    }

    /* 分页查询 */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Spec>> findPage(@PathVariable(value = "page") Integer page,
                                           @PathVariable(value = "size") Integer size) {
        PageInfo pageInfo = specService.findPage(page, size);
        return new Result<>(true, StatusCode.OK, "分页查询成功", pageInfo);
    }

    /* 分页条件查询 */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Spec>> findPage(@PathVariable(value = "page") Integer page,
                                           @PathVariable(value = "size") Integer size,
                                           @RequestBody Spec spec) {
        PageInfo pageInfo = specService.findPage(page, size, spec);
        return new Result<>(true, StatusCode.OK, "分页条件查询成功", pageInfo);
    }

}
