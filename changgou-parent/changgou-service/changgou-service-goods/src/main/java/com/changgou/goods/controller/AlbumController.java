package com.changgou.goods.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.goods.pojo.Album;
import com.changgou.goods.service.AlbumService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;


    /* 查询所有相册信息 */
    @GetMapping
    public Result<List<Album>> findAll() {
        List albums = albumService.findAll();
        return new Result<List<Album>>(true, StatusCode.OK, "查找成功", albums);
    }

    /* 根据 ID 查询信息 */
    @GetMapping("/{id}")
    public Result<Album> findById(@PathVariable(value = "id")Long id){
        Album album=albumService.findById(id);
        return new Result<>(true,StatusCode.OK,"查询成功",album);
    }

    /* 添加相册信息 */
    @PostMapping
    public Result add(@RequestBody Album album) {
        albumService.add(album);
        return new Result(true, StatusCode.OK, "插入成功");
    }

    /* 删除相册信息 */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable(value = "id") Long id) {
        albumService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /* 更新 */
    @PutMapping("/{id}")
    public Result update(@PathVariable(value = "id") Long id, @RequestBody Album album) {
        album.setId(id);
        albumService.update(album);
        return new Result(true, StatusCode.OK, "更新成功");
    }

    /* 分页查询 */
    @GetMapping("/search/{page}/{size}")
    public Result<PageInfo<Album>> findPage(@PathVariable(value = "page") int page,
                                            @PathVariable(value = "size") int size) {

        PageInfo<Album> pageInfo = albumService.findPage(page, size);
        return new Result<>(true, StatusCode.OK, "分页查询成功", pageInfo);
    }

    /* 条件查询 */
    @PostMapping("/search")
    public Result<List<Album>> findList(@RequestBody Album album) {
        List<Album> albums = albumService.findList(album);
        return new Result<>(true, StatusCode.OK, "条件查询成功", albums);
    }


    /* 分页条件查询 */
    @PostMapping("/search/{page}/{size}")
    public Result<PageInfo<Album>> findPage(@PathVariable(value = "page") int page,
                                            @PathVariable(value = "size") int size,
                                            @RequestBody Album album) {

        PageInfo<Album> pageInfo = albumService.findPage(page, size, album);
        return new Result<>(true, StatusCode.OK, "分页条件查询成功", pageInfo);
    }
}
