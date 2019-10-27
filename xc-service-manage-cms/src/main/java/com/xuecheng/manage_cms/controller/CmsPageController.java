package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequset;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmsPageControllerApi {
    @Autowired
    PageService pageService;


    @GetMapping("/list/{page}/{size}")
    @Override
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequset queryPageRequest) {
       /*//暂时用静态数据
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<>();
        //设置对象对象立面你的两个参数
        List<CmsPage> list = new ArrayList<>();
        CmsPage cmsPage = new CmsPage();
        cmsPage.setPageName("这是测试页面");
        cmsPage.setDataUrl("www.baidu.com");
        list.add(cmsPage);
        //设置设合计数
        cmsPageQueryResult.setTotal(2);
        cmsPageQueryResult.setList(list);

        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
        return queryResponseResult;*/
       if (page==0){
           page=1;
       }
       if (size==0){
           size=0;
       }



       return pageService.findList(page, size, queryPageRequest);



    }
}
