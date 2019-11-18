package com.xuecheng.api.cms;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Api(value = "测试静态化页面" ,description ="测试页面能否正常静态化" )
public interface CmsPagePreviewControllerApi {

    @ApiOperation("测试页面静态化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageId", value = "页面Id", required = true, paramType = "path", dataType = "String"),
    })
    public void preview(@PathVariable("pageId")String pageId) throws IOException;

}
