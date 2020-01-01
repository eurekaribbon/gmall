package com.atguigu.gmall.manage.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author lvlei
 * create on 2020-01-01-17:11
 */
public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {
        String imageUrl = "http://file.gmall.com";

        //String file = PmsUploadUtil.class.getResource("").getPath();
        try {
            ClientGlobal.init("D:\\java基础demo\\gmall\\gmall-manage-web\\src\\main\\resources\\tracker.conf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrackerClient client = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = client.getTrackerServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StorageClient storageClient = new StorageClient(trackerServer,null);
        try {
            byte[] bytes = multipartFile.getBytes();
            String originalFilename = multipartFile.getOriginalFilename();
            String subfix = originalFilename.substring(originalFilename.lastIndexOf("." )+1);
            String[] uploadInofs = storageClient.upload_file(bytes, subfix, null);

            for (String uploadInof : uploadInofs) {
                imageUrl+="/"+uploadInof;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrl;
    }
}
