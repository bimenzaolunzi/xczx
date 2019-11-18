package com.xuecheng.manage_cms;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTemplateTest {

    @Autowired
    GridFsTemplate gridFsTemplate;

    @Autowired
    GridFSBucket gridFSBucket;

    //存文件
    @Test
    public void testStore() throws FileNotFoundException {
        //定义FIle
        File file = new File("d:\\index_banner.ftl");
        //定义fileInputStream
        FileInputStream inputStream = new FileInputStream(file);
        ObjectId store = gridFsTemplate.store(inputStream, "index_banner.ftl");
        System.out.println("store = " + store);



    }

    //读取文件
    @Test
    public void queryFile() throws IOException {
        //根据文件ID查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5dcd03e244965452f43098eb")));

        //打开一个下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFSResource对象,获取流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);
        //从流中出数据
        String string = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
        System.out.println("string = " + string);

    }

}
