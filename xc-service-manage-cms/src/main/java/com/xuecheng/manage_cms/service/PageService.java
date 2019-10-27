package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequset;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_cms.dao.CmsPageReository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PageService {

    @Autowired
    CmsPageReository cmsPageReository;

    /**
     * 暴露接口一定要告诉别人从第几页开始
     *
     * @param page             页码从1开始计数
     * @param size             每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequset queryPageRequest) {
        //分页参数
        if (page <= 0) {
            page = 1;
        }
        if (size <= 0) {
            size = 10;
        }
        //因为page传给dao的时候一定是0开始的
        page = page - 1;
        PageRequest pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageReository.findAll(pageable);
        //QueryResponseResult里面需要两个参数,一个返回查询解决,一个是查询的结果
        QueryResult<CmsPage> packageQueryResult = new QueryResult<>();
        //先查看QueryResult里面的成员变量,一个list一个是total
        packageQueryResult.setList(all.getContent());//数据列表
        packageQueryResult.setTotal(all.getTotalPages());//数据总记录数


        return new QueryResponseResult(CommonCode.SUCCESS, packageQueryResult);



    }

}
