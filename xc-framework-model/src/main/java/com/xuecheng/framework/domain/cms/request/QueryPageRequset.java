package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 方便日久扩展用模型来展示
 */
@Data
public class QueryPageRequset {
    //接收页面查询的条件
    @ApiModelProperty("站点ID")
    //站点ID
    private String siteId;
    //页面id
    private String pageId;
    //页面名称
    private String pageName;
    //别名
    @ApiModelProperty("页面别名")
    private String pageAliase;
    //模板id
    private String templateId;
}
