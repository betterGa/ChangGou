package com.changgou.token;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 令牌的创建和解析
 */
public class CreateJWTTestDemo {

    /**
     * 创建令牌，用私钥加盐，加密算法是非对称加密
     */
    @Test
    public void testCreateToken(){
        
        // 加载证书
        ClassPathResource resource=new ClassPathResource("changgou.jks");
        
        // 读取证书数据
        KeyStoreKeyFactory keyStoreKeyFactory=new KeyStoreKeyFactory(resource,"changgou".toCharArray());
        
        // 获取一对密钥
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair("changgou","changgou".toCharArray());

        // 获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();

        // 创建令牌，使用私钥加盐，非对称加密
        Map<String,Object> payload=new HashMap<>();
       /* payload.put("key1","value1");
        payload.put("key2","value2");
        payload.put("key3","value3");*/

        payload.put("authorities",new String[]{"accountant","user","salesman"});
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(payload), new RsaSigner(aPrivate));

        // 获取令牌
        String token=jwt.getEncoded();

        System.out.println(token);
    }
}

