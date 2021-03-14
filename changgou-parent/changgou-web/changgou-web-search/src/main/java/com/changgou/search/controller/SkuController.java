package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
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
      /*  if(searchMap==null||searchMap.size()==0){
            searchMap=new LinkedHashMap<String,String>();
            searchMap.put("keywords","华为");
        }*/

        Map resultMap=skuFeign.search(searchMap);
        model.addAttribute("result",resultMap);

        // 将搜索条件进行存储
        model.addAttribute("searchMap",searchMap);
        return "search";
    }
}
