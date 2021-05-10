package com.changgou.store.service.impl;

import com.changgou.store.dao.StoreMapper;
import com.changgou.store.pojo.Store;
import com.changgou.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    StoreMapper storeMapper;

    @Override
    public boolean add(Store store) {
        Store storeResult = storeMapper.selectByPrimaryKey(store);
        if (storeResult == null) {
            Date date = new Date();
            store.setUpdated(date);
            store.setCreated(date);
            // 设置状态未入驻
            store.setCenterStatus("0");

            storeMapper.insert(store);
            // 插入成功
            return true;
        }
        // 说明商户名已存在，返回插入失败
        return false;
    }

    @Override
    public Store checkLogin(String storeName, String password) {
        Store store=new Store();
        store.setStoreName(storeName);
        store.setPassword(password);

        List<Store> storeList=storeMapper.select(store);
        // 查询无果
        if(storeList==null||storeList.size()==0){
            return null;
        }else {
        return storeList.get(0);}
    }
}
