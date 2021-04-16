package com.changgou.weixinpay;

import com.github.wxpay.sdk.WXPayUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/***
 * 微信支付 sdk 相关测试
 */
public class WeixinPayTest {

    @Test
    public void test() throws Exception {

        // 生成随机字符
        System.out.println("随机字符串" + WXPayUtil.generateNonceStr());

        // 将 Map 转化成 XML
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("id", "No.1");
        dataMap.put("title", "畅购商城");
        dataMap.put("money", "520");
        String xml = WXPayUtil.mapToXml(dataMap);
        System.out.println(xml);

        // 生成签名
        System.out.println("带签名的字符串" + WXPayUtil.generateSignedXml(dataMap, "secret"));

        // 将 XML 转化成 Map
       System.out.println(WXPayUtil.xmlToMap(xml));
    }
}
