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
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTYxNzI1NzAzNywiYXV0aG9yaXRpZXMiOlsic2Vja2lsbF9saXN0IiwiZ29vZHNfbGlzdCJdLCJqdGkiOiJmODA3NDQyYi0wZDQzLTQ5ZDQtYWMxNi02MGNiZDIzYmZhNTkiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoiamlhIn0.F4NL1CXuogj03CId3KQUDEu9cVl--vF115IrlnbmaKHAZnV7u0kOZYCxmgwq_Q_IyQ9xggv6Mv3cBjM6wl-5PfRAWxHwTdhlW6fIB8qvWJgCs9mhwagImyBG7JZNxs2h_Lg8_ZZFWBCNYE1hke71FzuLjI9onGfCfeuBcDoo2zf9KrajaF84uYrL4ck-FHqUu7ShCyl_VwlvXP9ssSr5EYmBD6ACVQOQnbffcT47-mIKcRQVE576ytKE97T2eUChFIqQBjAsc0ziqRUnQ8bqQi4fhc3uVj91DcpRwYD3lBkbPaxvDDB892jPT6tLOe3B0fU_60_1FCcE_wtuHNBbfQ";

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
