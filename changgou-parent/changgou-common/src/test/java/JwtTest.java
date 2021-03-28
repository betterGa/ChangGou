import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {
    @Test
    /**
     * 创建 JWT 令牌
     */
    public void testCreateJwt(){
        JwtBuilder builder= Jwts.builder()
                // 设置唯一编号
                .setId("888")

                // 设置主题，可以是 JSON 数据
                .setSubject("小白")

                // 设置过期时间，20s 后过期
                .setExpiration(new Date(System.currentTimeMillis()+200000))
                // 设置签发日期
                .setIssuedAt(new Date())

                // 设置签名，使用 HS256 算法，并设置 secretKey
                .signWith(SignatureAlgorithm.HS256,"itcast");

        // 自定义载荷信息
        Map<String,Object> userInfo=new HashMap<>();
        userInfo.put("company","jia");
        userInfo.put("address","Xian");
        userInfo.put("money","numberless");

        // 添加载荷信息
        builder.addClaims(userInfo);

        // 构建 并返回一个字符串
        System.out.println(builder.compact());
    }

    /**
     * 解析 JWT 令牌数据
     */
    @Test
    public void testParseJwt(){
        String token="eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI4ODgiLCJzdWIiOiLlsI_nmb0iLCJleHAiOjE2MTY5MjA4OTYsImlhdCI6MTYxNjkyMDY5NiwiYWRkcmVzcyI6IlhpYW4iLCJtb25leSI6Im51bWJlcmxlc3MiLCJjb21wYW55IjoiamlhIn0.qxd7D7DN2vE5ZlwKchuXNJdx3QA75tohTtUrVUuedqo";
        Claims claims=Jwts.parser()
                // 密钥（盐）
        .setSigningKey("itcast")

                // 要解析的令牌对象
        .parseClaimsJws(token)
                .getBody();
        System.out.println(claims);
    }
}
