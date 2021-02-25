package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Brand;
import com.changgou.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@RestController
@RequestMapping(value = "/brand")
@CrossOrigin
public class BrandController {

    @Autowired
    BrandService brandService;

    /* 查询所有品牌 */
    @GetMapping
    public Result<List<Brand>> findAll() {
        List<Brand> brands = brandService.findAll();
        return new Result<List<Brand>>(true, StatusCode.OK, "查询品牌集合成功!", brands);
    }

    /* 根据 ID 查询 */
    @GetMapping(value = "/{id}")
    public Result<Brand> findById(@PathVariable(value = "id") Integer id) {
        Brand brand = brandService.findById(id);
        return new Result<Brand>(true, StatusCode.OK, "根据 ID 查询成功！", brand);
    }

    /* 增加品牌 */
    @PostMapping
    public Result add(@RequestBody Brand brand) {
        brandService.add(brand);
        return new Result(true, StatusCode.OK, "增加品牌成功！");
    }

    /* 根据 ID 修改 */
    @PutMapping("/{id}")
    public Result update(@PathVariable(value = "id") Integer id, @RequestBody Brand brand) {
        brand.setId(id);
        brandService.update(brand);
        return new Result(true, StatusCode.OK, "修改成功！");
    }

    /* 根据 ID 删除*/
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable(value = "id") Integer id) {
        brandService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功！");
    }

    /* 条件查询 */
    @PostMapping("/search")
    public Result<List<Brand>> findList(@RequestBody Brand brand) {
        List<Brand> list = brandService.findList(brand);
        return new Result<>(true, StatusCode.OK, "查询品牌集合成功!", list);
    }

    /* 分页查询 */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@PathVariable(value="page")Integer page,
                                    @PathVariable(value = "size")Integer size) {
       PageInfo<Brand> brandPageInfo= brandService.findPage(page,size);
       return new Result<PageInfo<Brand>>(true,StatusCode.OK,"分页查询成功",brandPageInfo);
    }

    /* 条件分页查询 */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Brand>> findPage(@RequestBody Brand brand,
                                            @PathVariable(value="page")Integer page,
                                            @PathVariable(value = "size")Integer size) {

        // int i=1/0;
        PageInfo<Brand> brandPageInfo= brandService.findPage(brand,page,size);
        return new Result<PageInfo<Brand>>(true,StatusCode.OK,"条件分页查询成功",brandPageInfo);
    }

    @GetMapping("/category/{id}")
    public Result<List<Brand>> findBrandByCategory(@PathVariable(name="id") Integer id){
        List<Brand> brandList = brandService.findByCategory(id);

        return new Result<List<Brand>>(true,StatusCode.OK,"查询品牌列表成功",brandList);

    }
}