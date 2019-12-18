package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.manage_cms_client.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceTest {

    @Autowired
    PageService pageService;

    /**
     * 查询所有
     */
    @Test
    public void testGetHtml() {
        String pageHtml = pageService.getPageHtml("5dbfeb368baf1909346358dc");
        System.out.println("pageHtml = " + pageHtml);

    }


}
