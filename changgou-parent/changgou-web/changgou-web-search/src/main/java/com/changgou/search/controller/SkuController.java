package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;

@Controller
@RequestMapping(value = "/search")
public class SkuController {
    @Autowired
    private SkuFeign skuFeign;

    @GetMapping("/list")
    /**
     * 搜索
     * @param searchMap
     * @param model
     * @return
     */
    public String search(@RequestParam(required = false) Map<String,String> searchMap, Model model){
        Map resultMap=skuFeign.search(searchMap);
        model.addAttribute("result",resultMap);

        // 将搜索条件进行存储
        model.addAttribute("searchMap",searchMap);

        // 获取上次请求地址
        String[] urls=url(searchMap);
        model.addAttribute("url",urls[0]);
        model.addAttribute("sorturl",urls[1]);

        return "search";
    }

    /**
     * 获取 URL
     * @param searchMap
     * @return
     */
    public String[] url(Map<String,String> searchMap){
        // 初始路径,不带排序参数的
        String url="/search/list";

        // 带排序参数的
        String sorturl="/search/list";

        // 拼接
        if(searchMap!=null||searchMap.size()>0){
            url+="?";
            sorturl+="?";
            for(Map.Entry<String,String> entry:searchMap.entrySet()){
                String key=entry.getKey();
                String value=entry.getValue();

                // 遇到排序参数时，url 需要对它进行拼接，而 sorturl 不需要
                if(key.equalsIgnoreCase("sortField")||key.equalsIgnoreCase("sortRule")){
                    url+=key+"="+value+"&";
                }

                // 非排序参数，url 和 sorturl 都需要拼接
                else {
                    url+=key+"="+value+"&";
                    sorturl+=key+"="+value+"&";
                }
            }

            // 去掉最后一个 "&"
            url=url.substring(0,url.length()-1);
            sorturl=sorturl.substring(0,sorturl.length()-1);
        }
        return new String[]{url,sorturl};
    }
}
