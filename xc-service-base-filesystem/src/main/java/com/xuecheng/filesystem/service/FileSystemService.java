package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class FileSystemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String tracker_servers;
    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connect_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int network_timeout_in_seconds;
    @Value("${xuecheng.fastdfs.charset}")
    String charset;
    @Autowired
    FileSystemRepository fileSystemRepository;
    //上传文件
    public UploadFileResult upload(MultipartFile multipartFile,
                                    String filetag,
                                    String businesskey,
                                    String metadata){
        if (multipartFile==null){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }
        //第一步:将文件上传到fastDFS中,得到一个文件id.
        String fileId = fdfs_upload(multipartFile);
        if (StringUtils.isEmpty(fileId)){
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_SERVERFAIL);
        }
        //第二步:将文件id及其它文件信息储存到mongodb中.
        FileSystem fileSystem = new FileSystem();
        fileSystem.setFileId(fileId);
        fileSystem.setFilePath(fileId);
        fileSystem.setFiletag(filetag);
        fileSystem.setBusinesskey(businesskey);
        fileSystem.setFileName(multipartFile.getOriginalFilename());
        fileSystem.setFileType(multipartFile.getContentType());
        if (StringUtils.isNotEmpty(metadata)){
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileSystemRepository.save(fileSystem);
        return new UploadFileResult(CommonCode.SUCCESS,fileSystem);
    }

    /**
     *
     * @param multipartFile 文件本身
     * @return 文件id
     */
    //上传文件到fastDFS
    private String fdfs_upload(MultipartFile multipartFile){
        //初始化fastDFS环境
        initFdfsConfig();
        //创建trackerClie
        TrackerClient trackerClient=new TrackerClient();
        try {
            TrackerServer trackerServer = trackerClient.getConnection();
            //得到storage服务器
            StorageServer storeStorage = trackerClient.getStoreStorage(trackerServer);
            //创建storageClient来上传文件
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storeStorage);
            //上传文件
            //得到文件的字节
            byte[] fileBytes = multipartFile.getBytes();
            //得到文件的原始名称
            String originalFilename = multipartFile.getOriginalFilename();
            //得到扩展名
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String fileId = storageClient1.upload_file1(fileBytes, ext, null);
            return fileId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //初始化fastDFS环境
    private void initFdfsConfig(){
        try {
            //初始化tracker服务器,多个tracker中间以半角逗号分隔
            ClientGlobal.initByTrackers(tracker_servers);
            ClientGlobal.setG_charset(charset);
            ClientGlobal.setG_network_timeout(network_timeout_in_seconds);
            ClientGlobal.setG_connect_timeout(connect_timeout_in_seconds);
        } catch (Exception e) {
            e.printStackTrace();
            //抛出异常
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }
}
