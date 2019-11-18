package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "Cms配置管理接口", description = "cms配置管理接口,提供数据模型的管理,查询接口")
public interface CmsConfigControllerApi {

    @ApiOperation("根据ID查询CMS配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "序号", required = true, paramType = "path", dataType = "String")
    })
    public CmsConfig getModel(String id);
}
