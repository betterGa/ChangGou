package com.changgou.user.service.impl;

import com.changgou.user.dao.StoreMapper;
import com.changgou.user.pojo.Store;
import com.changgou.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.x500.X500Principal;
import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    StoreMapper storeMapper;

    // 查看未入驻商家列表
    @Override
    public List<Store> unCenterList() {
        Store store=new Store();
        // 未入驻
        store.setCenterStatus("0");
        return storeMapper.select(store);
    }

    // 查看已入驻商家列表
    @Override
    public List<Store> centerList() {
        Store store=new Store();
        // 已入驻
        store.setCenterStatus("1");
        return storeMapper.select(store);
    }

    // 允许入驻
    @Override
    public void permitCenter(String storeName) {
        Store store=storeMapper.selectByPrimaryKey(storeName);
        store.setCenterStatus("1");
        Date date=new Date();
        // 设置修改状态时间
        store.setDatetime(date);
        storeMapper.updateByPrimaryKey(store);
    }

    // 修改使用状态
    @Override
    public void restrict(String storeName) {
        Store store=storeMapper.selectByPrimaryKey(storeName);
        Date date=new Date();
        // 设置修改状态时间
        store.setDatetime(date);
        String status = store.getStatus();
        if(status.equals("0")){
            store.setStatus("1");
        }else store.setStatus("0");
        storeMapper.updateByPrimaryKey(store);
    }

    // 查询商家列表
    @Override
    public List<Store> showList() {
        return storeMapper.selectAll();
    }
}
