package com.xuecheng.test.freemarker.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequestMapping("/freemarker")
@Controller
public class FreemarkerController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("banner")
    public String test02(Map<String, Object> map) {

        //使用restTemplate请求轮播图的数据模型
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        System.out.println("body = " + body);
        //设置模型数据
        map.putAll(body);
        return "index_banner";
    }


    //测试1
    @RequestMapping("test1")
    public String test01(Map<String, Object> map) {
        map.put("name", "广州电信云计算中心");
        return "test1";
    }


}
