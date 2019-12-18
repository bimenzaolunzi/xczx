package com.xuecheng.manage_cms_client;


import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
public class FreemarkerTest {



    //测试静态化,给予ftl模板文件生产html
    @Test
    public void testGenerateHtml() throws IOException, TemplateException {
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //得到classpath的路径  "/"代表获取当前类的根路径
        String path = this.getClass().getResource("/").getPath();
        //定义模板路径
        configuration.setDirectoryForTemplateLoading(new File(path+"templates"));
        //获取模板文件内容
        Template template = configuration.getTemplate("test1.ftl");
        //定义数据模型
        Map map = this.getmap();
        //静态化
        String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        InputStream inputStream = IOUtils.toInputStream(content);
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\1.html"));
         IOUtils.copy(inputStream, outputStream);
         inputStream.close();
         outputStream.close();

        System.out.println("string = " + content);



    }

    public Map getmap(){
        Map<String, Object> map = new HashMap<>();
        map.put("name","你好啊,广州1111");

        return map;
    }

    @Test
    public void testGenerateHtmlByString() throws IOException, TemplateException {
        //定义配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //定义模板
        //模板内容(字符串)
        //模板内容,这里测试时使用简单的字符串作为模板
        String templateString ="<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf‐8\">\n" +
                "    <title>Hello World!</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "Hello ${name}!\n" +
                "</body>\n" +
                "</html>";
        //使用一个模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("222",templateString);
        //配置类添加模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //取出模板
        Template template = configuration.getTemplate("222", "utf-8");
        Map getmap = this.getmap();
        //定义数据模型
        String string = FreeMarkerTemplateUtils.processTemplateIntoString(template, getmap);
        InputStream inputStream = IOUtils.toInputStream(string);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\2.html");
        IOUtils.copy(inputStream,fileOutputStream);
        inputStream.close();
        fileOutputStream.hashCode();

        //静态化

    }




}
