package com.changgou.store.service;

import com.changgou.store.pojo.Store;
import org.springframework.stereotype.Service;

public interface StoreService {
    // 添加商家信息
     boolean add(Store store);

     // 查找商家信息
    Store checkLogin(String storeName,String password);
}
