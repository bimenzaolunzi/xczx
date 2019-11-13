package com.xuecheng.test.freemarker.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/freemarker")
@Controller
public class FreemarkerController {

    //测试1
    @RequestMapping("test1")
    public String test01(Map<String, Object> map) {
        map.put("name", "广州电信云计算中心");
        return "test1";
    }


}
