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
       // String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjA0OTYwNjg4NiwiYXV0aG9yaXRpZXMiOlsiYWNjb3VudGFudCIsInVzZXIiLCJzYWxlc21hbiJdLCJqdGkiOiI2ODhhNzA2YS02ZDY5LTQ0ZTctOTExMy02Y2ZlN2NlM2NlZDkiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoic3ppdGhlaW1hIn0.d5rMz4aSMdrF-vT1roaLbzqJIAWTjoJ_EWATuq1lBch7tdtJaJB_yNjE8lOS9OlRwgkgqajLyon8ND7gid3IUyZ2Y_BjINbn0hPqToiR5i9DpB4Lpmn77JsTQmdXKLrQBxo9YLUzVk9VPUog0iSr0kTyeKvWJVSEgAXDN4my9f-dwre4BZry4BbBGflGBiTnFuZ1rQHvF_LkKe23DPC0BWIA2FEu6nsSC0G3fWLyhFFJRNZYXEcNBnNPQMiKSuV0j1ogBSpA4pPLzzO2CQICx5cEKvxEAnvC9ApLGqNBX8LiT6Mmo4x1pDNm1PToGByElEHetkr11RX0fwOszQ4q3A";
String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdXRob3JpdGllcyI6WyJhY2NvdW50YW50IiwidXNlciIsInNhbGVzbWFuIl19.ZRa6c2utOa6I6w4LRsYx8tIxjVG1T9WPSaNsnZYsEaUYiEPD8FLOwAK3AyrCDe0rdgDHQB9iFVtyRwQtazbMg4yLoj1lwhz03txHWfPZPGRgahW1qRjMB5b0pr1We4V561JCiA0F7YBuRDpV7yAzzNPVnGHhtCuETylc-XdHpzWbUva9ZEr13NFcN2KcuTstTMvLN2L6hMilAkKE_5wBnmo8jasTw4Hs5Dd4xrSeZ6waNSDiN4XeWk7_YPv59xD9fT_LakbamFwnWr7sCzZ-bhPLXBVdVBGtRqLR-mIqt9LFDuWrejn62VKETHeFsg_hDbzk3RGERFFvsuTx_EcBiw";

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
