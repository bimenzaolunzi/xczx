package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.request.QueryPageRequset;
import com.xuecheng.framework.model.response.QueryResponseResult;

public interface CmsPageControllerApi {
    //页面查询
    public QueryResponseResult findList(int page, int size, QueryPageRequset queryPageRequset);

}
