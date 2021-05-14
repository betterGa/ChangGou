package com.changgou.user.service;

import com.changgou.user.pojo.Store;

import java.util.List;

public interface AdminService {

    // 查询未入驻商家列表
    List<Store> unCenterList();

    // 查询入驻商家列表
    List<Store> centerList();

    // 允许入驻
    void permitCenter(String storeName);

    // 修改使用状态
    void restrict(String storeName);

    // 查询商家列表
    List<Store> showList();
}
