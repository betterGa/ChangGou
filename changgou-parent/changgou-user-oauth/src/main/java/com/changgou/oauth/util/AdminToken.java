package com.changgou.oauth.util;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class AdminToken {

    public static String adminToken() {
        // 加载证书
        ClassPathResource resource = new ClassPathResource("changgou.jks");

        // 读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "changgou".toCharArray());

        // 获取一对密钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("changgou", "changgou".toCharArray());

        // 获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();

        // 创建令牌，使用私钥加盐，非对称加密
        Map<String, Object> payload = new HashMap<>();

        payload.put("authorities", new String[]{"accountant", "user", "salesman"});
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(aPrivate));

        // 获取令牌
        String token = jwt.getEncoded();
        return token;
    }
}
