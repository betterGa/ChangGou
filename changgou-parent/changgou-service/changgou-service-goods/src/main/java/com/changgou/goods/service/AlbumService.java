package com.changgou.goods.service;

import com.changgou.goods.pojo.Album;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AlbumService {
    /* 查询所有相册信息 */
    public List<Album> findAll();

    /* 按照 ID 查询 */
    public Album findById(Long id);

    /* 添加相册信息 */
    public void add(Album album);

    /* 删除相册信息 */
    public void delete(Long id);

    /* 修改相册信息 */
    public void update(Album album);

    /* 分页查询 */
    public PageInfo findPage(int page,int size);

    /* 条件查询 */
    public List<Album> findList(Album album);

    /* 按条件分页查询 */
    public PageInfo findPage(int page,int size,Album album);
}
