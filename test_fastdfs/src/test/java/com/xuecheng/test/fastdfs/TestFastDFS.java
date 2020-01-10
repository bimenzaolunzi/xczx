package com.xuecheng.test.fastdfs;

import org.apache.commons.io.IOUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDFS {

    //上传测试
    @Test
    public void testUpLoad() {

        try {
            //加载fastdfs-client.properties文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient,用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接Tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建StorageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);
            //向storage服务器上传文件
            //本地文件的路径
            String filePath="E:/3.JPG";
            String fileId = storageClient1.upload_file1(filePath,"jpg",null);
            System.out.println("fileId = " + fileId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }

    }

    //下载测试
    @Test
    public void testDownLoad() {
        try {
            //加载fastdfs-client.properties文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient,用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接Tracker
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取Storage
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建StorageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer, storeStorage);
            //向storage服务器上传文件
            //本地文件的路径
            String fileId="group1/M00/00/00/wKjTgV4JuZuABsnoAAB_BxRy1P8223.jpg";
            byte[] bytes = storageClient1.download_file1(fileId);
            //使用输出流保存文件
            FileOutputStream fileOutputStream = new FileOutputStream(new File("E:/3.jpg"));
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
}
