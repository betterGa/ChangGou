package com.changgou.pay.service;

import com.github.wxpay.sdk.WXPay;

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


    /**
     * 生成 WXPay 对象
     * @return
     */
    WXPay wxPay();

    /**
     * 取消订单
     */
    Map cancelOrder(String outTradeNo) throws Exception;
}
