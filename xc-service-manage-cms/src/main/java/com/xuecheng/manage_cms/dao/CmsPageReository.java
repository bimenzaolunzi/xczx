package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CmsPageReository extends MongoRepository<CmsPage,String> {

    /**
     * 可以自定查询方法,中间用and连接,参数需要后期手动添加到形参里面
     * 注意的一点是,自定义查询方法,只能拼接熟悉,不能使用主键+属性查询,否则显示主键ID不能被对象封装起来
     * @param s
     * @param ss
     * @return
     */
    Optional<CmsPage> findBySiteIdAndPageAliase(String s,String ss);
}
