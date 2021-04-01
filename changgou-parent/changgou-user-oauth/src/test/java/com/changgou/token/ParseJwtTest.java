package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 解析令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkxIjoidmFsdWUxIiwia2V5MiI6InZhbHVlMiIsImtleTMiOiJ2YWx1ZTMifQ.M2-nMoL6drCxN2D-4bD7tUzBZK7FQwEVQ_677fmRUVfe7AOX4fUErfvBvZ3TXkKIvflWgE1ESnxJ0GYKaLre4qLn8JMyd0f6z1TGZ_0RQS5PJER8DcMxELtGtfllAjOa2wlM1Ui9pfB1IghlnRZzwsUma3sVwasOy9iIa4fiG8nI7MUDqaZSzWReO7IGKNvvIXvnW7NMKeFUolJDd84SNR-mLoJfaIH7Temcch0Xhk9ci01ly41eeOyJOvkzVlJQTNojwZGp3yL8QGA_hkxjw7LM3jViaJA0v-7NHVkkcEhvPsRHH2P4Xo3i2wfLcFonOsvnXhMTnr74_7j83gFh0w";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhCDQMSUvJ+5YcpTCPijbPWhuot8tOm2IPSZa96Mnr561Y6v4ckJejf58GneFrdn6eBaYXaoo4Cyge9LYzevHpPijBO4cnCt3dCBf2k1c/7adbhwfwVAE9sUsYSFbgOq4mobYXIMwcbdNeO0+Z+AVhUhv+nEBS+fUNkdV55WwvRDKG3pnuNnyMMBDj0XclJjDOfz2NNGignsVIiefPXhE0OdkAL6vIX89U9G5wUUbL87aPOCrvqEpF4jJKyDPQa1bRVATOo8EFWSmkhiAzTQwATvq716ZTTzpjrXJRQ/m4jNuSl0OT0rIDfjxjlfg1shQTXBCW/kHxmaCZ1BQrBQqywIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取 Jwt 原始内容 载荷
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
