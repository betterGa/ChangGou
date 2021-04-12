package com.changgou.filter;

public class URLFilter {

    // 要放行的路径
    private static final String noAuthorizeurls = "/api/user/add,/api/user/login";


    /**
     * 判断当前请求的地址是否在已有的不拦截的地址中,
     * 如果存在 则返回 true，表示不拦截
     * 否则返回 false
     *
     * @param uri 获取到的当前的请求的地址
     * @return
     */
    public static boolean hasAuthorize(String uri) {
        String[] split = noAuthorizeurls.split(",");

        for (String s : split) {
            if (s.equals(uri)) {
                return true;
            }
        }
        return false;
    }
}
