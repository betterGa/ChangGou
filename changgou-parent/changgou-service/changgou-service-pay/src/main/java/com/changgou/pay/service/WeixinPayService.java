package com.changgou.pay.service;

import java.util.Map;

public interface WeixinPayService {
    /***
     * 微信支付二维码生成
     */
    Map createNative(Map<String,String> parameterMap) throws Exception;


    /**
     * 查询订单
     * @param out_trade_no
     * @return
     */
     Map queryPayStatus(String out_trade_no);
}
