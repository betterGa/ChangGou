package com.changgou.pay.service.impl;

import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import com.netflix.ribbon.proxy.annotation.Http;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeixinPayService {

    // 应用 ID
    @Value("${weixin.appid}")
    private String appid;

    // 商户 ID
    @Value("${weixin.partner}")
    private String partner;

    // 密钥
    @Value("${weixin.partnerkey}")
    private String partnerkey;

    // 支付回调地址
    @Value("${weixin.notifyurl}")
    private String notifyurl;


    /**
     * 创建付款二维码
     *
     * @param parameterMap
     * @return
     */
    @Override
    public Map createNative(Map<String, String> parameterMap) throws Exception {

        /**
         * 封装参数
         */
        Map<String, String> paramMap = new HashMap<>();

        // 公众账号 ID
        paramMap.put("appid", appid);

        // 商户号
        paramMap.put("mch_id", partner.trim());

        // 随机字符串
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

        // 商品描述
        paramMap.put("body", "畅购商城");

        // 商品订单号
        paramMap.put("out_trade_no", parameterMap.get("outtradeno"));

        // 标价金额
        paramMap.put("total_fee", parameterMap.get("totalfee"));

        // 终端 IP
        paramMap.put("spbill_create_ip", "127.0.0.1");

        // 通知地址
        paramMap.put("notify_url", notifyurl);

        // 交易类型
        paramMap.put("trade_type", "NATIVE");

        // 传入密钥，生成的 xml 中就会带有签名 sign 信息
        String xmlParameters = WXPayUtil.generateSignedXml(paramMap, partnerkey);
        System.out.println("xml:"+xmlParameters);

        /**
         * URL
         */
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";


        /**
         * 提交方式
          */
        HttpClient httpClient = new HttpClient(url);
        httpClient.setHttps(true);


        /**
         * 提交参数
         */
        httpClient.setXmlParam(xmlParameters);

        /**
         * 执行请求
         */
        httpClient.post();

        // 返回数据
        String content = httpClient.getContent();

        Map<String,String> resultMap=WXPayUtil.xmlToMap(content);
        return resultMap;
    }

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1.封装参数
            Map param = new HashMap();
            param.put("appid",appid);                            //应用ID
            param.put("mch_id",partner);                         //商户号
            param.put("out_trade_no",out_trade_no);              //商户订单编号
            param.put("nonce_str",WXPayUtil.generateNonceStr()); //随机字符

            //2、将参数转成 xml 字符，并携带签名
            String paramXml = WXPayUtil.generateSignedXml(param,partnerkey);

            //3、发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //4、获取返回值，并将返回值转成 Map
            String content = httpClient.getContent();
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
