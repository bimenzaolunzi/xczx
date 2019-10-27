package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageReositoryTest {

    @Autowired
    CmsPageReository cmsPageReository;

    /**
     * 查询所有
     */
    @Test
    public void testfindAll() {
        //上面是接口的类型,因为spring会根据他的接口生成实现类
        List<CmsPage> all = cmsPageReository.findAll();
        System.out.println("all = " + all);

    }

    /**
     * 分页查询
     */
    @Test
    public void testfindPage() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageReository.findAll(pageable);
        System.out.println("all = " + all);
    }

    /**
     * 保存的方法
     */
    @Test
    public void testSave() {
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s001");
        cmsPage.setDataUrl("这是测试路劲");
        cmsPage.setPageName("测试页面");
        List<CmsPageParam> list = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        list.add(cmsPageParam);
        cmsPage.setPageParams(list);
        cmsPageReository.save(cmsPage);
        System.out.println("cmsPage = " + cmsPage);

    }

    /**
     * 根据主键删除
     */
    @Test
    public void testdeleye() {
        cmsPageReository.deleteById("5db2bc744ddfe42f5c3eba13");
    }

    /**
     * 修改
     */
    @Test
    public void testupdata() {

        //查询对象,根据id查询
        Optional<CmsPage> cmsPage = cmsPageReository.findById("5db2bdfe4ddfe43860a9ec94");
        /**
         * optional是jdk1.8引入的类型,他是一个容器包括了我们所需要的对象,使用isPresrnt方法判断是否为空
         * 优点:
         * 1.提醒我们非空判断
         * 2.将对象非空检测标准化
         */
        if (cmsPage.isPresent()) {
            CmsPage cmsPage1 = cmsPage.get();
            //设置要修改的值
            cmsPage1.setPageAliase("这是修改后的属性值");
            cmsPage1.setPageWebPath("/s/s/s/s/s");
            //修改
            cmsPageReository.save(cmsPage1);
        } else {
            System.out.println("这个集合为空");
        }

    }

    /**
     * 自定义方法
     */
    @Test
    public void test111() {
        Optional<CmsPage> optional = cmsPageReository.findBySiteIdAndPageAliase("s001", "这是修改后的属性值");
        if (optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            System.out.println("cmsPage = " + cmsPage);
        } else {
            System.out.println("这个集合为空");
        }
    }

}
