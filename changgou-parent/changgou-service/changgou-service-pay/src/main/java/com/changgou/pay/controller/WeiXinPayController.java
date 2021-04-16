package com.changgou.pay.controller;

import com.changgou.pay.service.WeixinPayService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "/weixin/pay")
@CrossOrigin
public class WeiXinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    /**
     * 生成付款二维码
     *
     * @param parameterMap
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/create/native")
    Result createNative(@RequestParam Map<String, String> parameterMap) throws Exception {
        Map resultMap = weixinPayService.createNative(parameterMap);
        return new Result(true, StatusCode.OK, "创建二维码预付订单成功！", resultMap);
    }

    /***
     * 查询支付状态
     * @param outtradeno
     * @return
     */
    @GetMapping(value = "/status/query")
    public Result queryStatus(String outtradeno) {
        Map<String, String> resultMap = weixinPayService.queryPayStatus(outtradeno);
        return new Result(true, StatusCode.OK, "查询状态成功！", resultMap);
    }

    /***
     * 支付信息回调
     * @param request
     * @return
     */
    @RequestMapping(value = "/notify/url")
    public String notifyUrl(HttpServletRequest request){
        String result="<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
        return result;
    }

}
