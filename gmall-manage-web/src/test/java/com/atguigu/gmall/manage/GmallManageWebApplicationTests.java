package com.atguigu.gmall.manage;


import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallManageWebApplicationTests {


    @Test
     public void contextLoads() throws IOException, MyException {
        //String file = GmallManageWebApplicationTests.class.getResource("tracker.conf").getPath();
        ClientGlobal.init("D:\\java基础demo\\gmall\\gmall-manage-web\\src\\main\\resources\\tracker.conf");
        TrackerClient client = new TrackerClient();
        TrackerServer trackerServer = client.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer,null);

        String[] jpgs = storageClient.upload_file("C:\\Users\\Administrator\\Desktop\\d98ace2a5139a00c.jpg", "jpg", null);
        String url = "http://file.gmall.com";
        for (String jpg : jpgs) {
            url+="/"+jpg;
        }
        //M00/00/00/wKgMgl4MWemAM6aKAAklTp1uigg782.jpg
        System.out.println(url);
    }

}
