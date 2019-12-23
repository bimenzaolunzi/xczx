package com.xuecheng.manage_cms_client.controller;

import com.xuecheng.api.cms.CmsPageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequset;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms_client.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        return pageService.findList(page, size, queryPageRequest);
    }

    /**
     * RequestBody 作用是将前台传过来的json数据转换成对象
     *
     * @param cmsPage
     * @return
     */
    @PostMapping("/add")
    @Override
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);


    }

    //根据页面ID查询页面信息
    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.findById(id);
    }

    ;

    //修改页面

    /**
     * 根据id查询页面,再通过传入新的页面,后台进行修改
     *
     * @param id
     * @param cmsPage
     * @return
     */
    @PutMapping("/edit/{id}")//这里使用put方法,http方法中put表示更新
    public CmsPageResult edit(@PathVariable("id") String id, @RequestBody CmsPage cmsPage) {
        return pageService.update(id, cmsPage);
    }

    /**
     * 根据也面的ID删除页面,提示删除之前先查询页面是否存在
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    @Override
    public ResponseResult delete(@PathVariable("id") String id) {
        return pageService.delete(id);
    }

    @PostMapping("postPage/{pageId}")
    @Override
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.postPage(pageId);
    }


}
