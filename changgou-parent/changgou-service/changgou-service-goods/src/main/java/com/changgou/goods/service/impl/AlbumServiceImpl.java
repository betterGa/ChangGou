package com.changgou.goods.service.impl;

import com.changgou.goods.dao.AlbumMapper;
import com.changgou.goods.pojo.Album;
import com.changgou.goods.service.AlbumService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService {

    @Autowired
    AlbumMapper albumMapper;

    @Override
    public List<Album> findAll() {
        return albumMapper.selectAll();
    }

    @Override
    public Album findById(Long id) {
        return albumMapper.selectByPrimaryKey(id);
    }


    @Override
    public void add(Album album) {
        albumMapper.insert(album);
    }

    @Override
    public void delete(Long id) {
        albumMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Album album) {
        albumMapper.updateByPrimaryKey(album);
    }

    /* 分页查询 */
    @Override
    public PageInfo findPage(int page, int size) {
        PageHelper.startPage(page, size);
        List<Album> albums = albumMapper.selectAll();
        return new PageInfo(albums);
    }

    /* 把构建条件的逻辑提取出来 */
    public static Example createExample(Album album) {
        // 构建条件
        Example example = new Example(Album.class);
        Example.Criteria criteria = example.createCriteria();
        if (album != null) {
            // 对 id、title、image、imageItems 属性进行模糊查询
            if (!StringUtils.isEmpty(album.getId())) {
                criteria.andEqualTo("id", album.getId());
            }
            if (!StringUtils.isEmpty(album.getTitle()))
                criteria.andLike("title", "%" + album.getTitle() + "%");
            if (!StringUtils.isEmpty(album.getImage()))
                criteria.andLike("image", "%" + album.getImage() + "%");
            criteria.andLike("title", "%" + album.getTitle() + "%");
            if (!StringUtils.isEmpty(album.getImageItems()))
                criteria.andLike("image_items", "%" + album.getImageItems() + "%");
        }
        return example;
    }

    /* 条件查询 */
    @Override
    public List<Album> findList(Album album) {

        return albumMapper.selectByExample(createExample(album));
    }


    /* 分页条件查询 */
    @Override
    public PageInfo findPage(int page, int size, Album album) {

        // 分页
        PageHelper.startPage(page, size);

        List albums = albumMapper.selectByExample(createExample(album));
        return new PageInfo(albums);
    }

}
