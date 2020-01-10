package com.xuecheng.manage_cms_client.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequset;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms_client.config.RabbitmqConfig;
import com.xuecheng.manage_cms_client.dao.CmsConfigRepository;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    CmsPageRepository cmsPageRepository;
    @Autowired
    CmsConfigRepository cmsConfigReository;
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    GridFSBucket gridFSBucket;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 暴露接口一定要告诉别人从第几页开始
     *
     * @param page             页码从1开始计数
     * @param size             每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequset queryPageRequest) {
        //首先判断queryPageRequest是否为空,为空就复赋值
        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequset();
        }
        //自定义条件查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        CmsPage cmsPage = new CmsPage();
        //设置条件值 站点id ,queryPageRequest.getSiteId()这行代码有空指针风险,所以需要在上面进行重新判断new对象
        if (StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置模板DI  注意工具类的方法名字,isNotEmpty和isEmpty
        if (StringUtils.isNotEmpty(queryPageRequest.getTemplateId())) {
            cmsPage.setTemplateId(queryPageRequest.getTemplateId());
        }
        //设置页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //分页参数
        if (page <= 0) {
            page = 1;
        }
        //因为page传给dao的时候一定是0开始的
        page = page - 1;
        if (size <= 0) {
            size = 10;
        }

        PageRequest pageable = PageRequest.of(page, size);
        //传入查询的例子模板
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //实现自定义条件查询并且分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
        if (all.getContent().size() <= 0) {
            return new QueryResponseResult(CommonCode.SERVER_ERROR, null);
        }


        //QueryResponseResult里面需要两个参数,一个返回查询解决,一个是查询的结果
        QueryResult<CmsPage> packageQueryResult = new QueryResult<>();
        //先查看QueryResult里面的成员变量,一个list一个是total
        packageQueryResult.setList(all.getContent());//数据列表
        packageQueryResult.setTotal(all.getTotalElements());//数据总记录数
        return new QueryResponseResult(CommonCode.SUCCESS, packageQueryResult);
    }

    /**
     * 新增页面
     *
     * @param cmsPage 页面信息
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage) {
        if (cmsPage == null) {
            //抛出异常,非法参数异常
            ExceptionCast.cast(CmsCode.CMS_COURSE_PARAMETERISNULL);
        }
        //保存页面之前需要先判断页面是否存在
        //根据页面名称,站点ID,页面路径.去cmspage集合,如果查到说明此页面已经存在,如果查询不到再继续添加
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        if (cmsPage1 != null) {
            //页面已经存在
            //抛出异常,异常内容是页面已经存在
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        //因为mogodb的主键是自增,为了防止别人给我设置主键,我将cmspage之间设置为空
        cmsPage.setPageId(null);
        //如果查询的对象为空,那么久保存
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS, cmsPage);
    }

    //根据页面ID查询页面信息
    public CmsPage findById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);
        //判断选项是否存在
        if (byId.isPresent()) {
            CmsPage cmsPage = byId.get();
            return cmsPage;
        }
        return null;
    }

    ;

    //修改页面
    public CmsPageResult update(String id, CmsPage cmsPage) {
        //根据ID从数据库查询信息
        CmsPage cmsPage1 = this.findById(id);
        if (cmsPage1 != null) {
            //准备更新书库
            //设置要修改的书库
            //模板id
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
            //所属站点
            cmsPage1.setSiteId(cmsPage.getSiteId());
            //别名
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
            //页面名称
            cmsPage1.setPageName(cmsPage.getPageName());
            //web访问路径
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
            //物理路径
            cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //数据URI
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
            //修改完成后保存
            CmsPage save = cmsPageRepository.save(cmsPage1);
            if (save != null) {
                return new CmsPageResult(CommonCode.SUCCESS, cmsPage1);
            }
        }
        //如果为空就修改失败
        return new CmsPageResult(CommonCode.FAIL, null);
    }


    public ResponseResult delete(String id) {
        //删除之前先查询页面是否存在
        CmsPage byId = this.findById(id);
        if (null != byId) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    public CmsConfig getConfigById(String id) {
        Optional<CmsConfig> optional = cmsConfigReository.findById(id);
        if (optional.isPresent()) {
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }
    //页面静态化方法

    /**
     * 1.静态化程序获取页面的DataURl
     * 2.静态化程序远程请求DataURL获取数据模型
     * 3.静态化程序获取页面的模板信息
     * 4.执行页面静态化
     */
    public String getPageHtml(String pageId) {
        //获取数据模型
        Map model = this.getModelByPageId(pageId);
        if (null == model) {
            //数据模型获取不到
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面模板信息
        String template = this.getTemplateByPageId(pageId);
        if (StringUtils.isEmpty(template)) {
            //模板为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = this.generateHtml(template, model);
        return html;
    }

    //执行静态化
    private String generateHtml(String templateContent, Map model) {
        //创一个配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //穿件模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板内容
        try {
            Template template = configuration.getTemplate("template", "UTF-8");
            //调用API进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            return content;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    //获取页面模板信息
    private String getTemplateByPageId(String pageId) {
        CmsPage cmsPage = this.findById(pageId);
        //cmsPage注意判断
        if (null == cmsPage) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> byId = cmsTemplateRepository.findById(templateId);
        if (byId.isPresent()) {
            CmsTemplate cmsTemplate = byId.get();
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFS获取模板文件内容
            //根据文件ID查询文件
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

            //打开一个下载流对象
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFSResource对象,获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
            //从流中出数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取数据模型 私有方法即可
    private Map getModelByPageId(String pageId) {
        CmsPage cmsPage = this.findById(pageId);
        //cmsPage注意判断
        if (null == cmsPage) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //获取dataURL
        String dataUrl = cmsPage.getDataUrl();
        //判断是是否有数据
        if (StringUtils.isEmpty(dataUrl)) {
            //返回错误信息
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //通过restTemplate请求dataURL获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    //页面发布
    public ResponseResult postPage(String pageId) {
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将页面静态化文件储存到GridFs中
        CmsPage cmsPage = saveHtml(pageId, pageHtml);
        //向MQ发消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    // 发送页面发布消息
    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.findById(pageId);
        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);
        String msg = JSON.toJSONString(msgMap);
        // 获取站点ID作为routingKey
        String siteId = cmsPage.getSiteId();
        // 发布消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);
    }

    // 保存静态页面内容
    private CmsPage saveHtml(String pageId, String htmlContent) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        CmsPage cmsPage = optional.get();
        //储存之前先删除
        String htmlFileId = cmsPage.getHtmlFileId();
        if (StringUtils.isNotEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }
        //保存html文件到GridFs
        InputStream inputStream = null;
        try {
            inputStream = IOUtils.toInputStream(htmlContent, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        // 文件Id
        String filedId = objectId.toString();
        //将文件Id储存到cmsPage中
        cmsPage.setHtmlFileId(filedId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }
}
