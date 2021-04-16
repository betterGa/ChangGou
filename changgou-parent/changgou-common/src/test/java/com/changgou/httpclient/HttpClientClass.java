package com.changgou.httpclient;


import entity.HttpClient;
import org.junit.Test;

import java.io.IOException;

public class HttpClientClass {
    /**
     * 测试 HttpClient 工具类的使用
     */
    @Test
    public void test() throws IOException {
        // 发送 Http 请求
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        HttpClient httpClient = new HttpClient(url);

        // 发送指定参数
        String xml = "<xml><name>用户</name></xml>";
        httpClient.setXmlParam(xml);

        // 使用 Https 协议
        httpClient.setHttps(true);

        // 发送请求
        httpClient.post();

        // 获取响应数据
        String result = httpClient.getContent();
        System.out.println(result);

    }
}
