package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Template;
import com.changgou.goods.service.TemplateService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/template")
@CrossOrigin
public class TemplateController {
    @Autowired
    TemplateService templateService;

    /* 增加模板信息 */
    @PostMapping
    public Result add(@RequestBody Template template) {
        templateService.add(template);
        return new Result(true, StatusCode.OK,"插入成功");
    }

    /* 删除模板信息 */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable(value = "id") Integer id) {
        templateService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /* 根据 ID 修改模板信息 */
    @PostMapping("/{id}")
    public Result update(@PathVariable(value = "id") Integer id, @RequestBody Template template) {
        // 传入的 template 信息是不含 id 的，所以需要设置
        template.setId(id);
        // 根据 id 更新，估计源码中又获取了一次 id 值
        templateService.update(template);
        return new Result(true,StatusCode.OK,"更新成功");
    }

    /* 查询所有信息 */
    @GetMapping
    public Result<List<Template>> findAll() {
        List<Template> templates= templateService.findAll();
        return new Result<>(true,StatusCode.OK,"查询成功",templates);
    }

    @GetMapping("/{id}")
    public Result<Template> findById(@PathVariable(value = "id") Integer id) {
        Template template=templateService.findById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",template);
    }

    /* 分页查询 */
    @GetMapping("/{page}/{size}")
    public Result<PageInfo<Template>> findPage(@PathVariable(value = "page") Integer page,
                                               @PathVariable(value = "size") Integer size) {
       PageInfo pageInfo=templateService.findPage(page,size);
       return new Result<>(true,StatusCode.OK,"分页查询成功",pageInfo);
    }

    /* 条件查询 */
    @PostMapping("/search")
    public Result<List<Template>> findList(@RequestBody Template template) {
        List<Template> templates=templateService.findList(template);
        return new Result<>(true,StatusCode.OK,"条件查询成功",templates);
    }

    /* 分页条件查询 */
    @PostMapping("/{page}/{size}")
    public Result<PageInfo<Template>> findPage(@PathVariable(value = "page") Integer page,
                                       @PathVariable(value = "size") Integer size,
                                       @RequestBody Template template){
      PageInfo pageInf= templateService.findPage(page,size,template);
      return new Result<>(true,StatusCode.OK,"分页条件查询成功",pageInf);
    }
}
