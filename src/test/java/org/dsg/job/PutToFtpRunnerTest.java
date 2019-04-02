package org.dsg.job;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.dsg.config.FtpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PutToFtpRunnerTest {

    @Autowired
    private PutToFtpRunner putToFtpRunner;


    public void ttt(){
        putToFtpRunner.sendFile();
    }

    @Autowired
    private FtpConfig ftpConfig;

    @Test
    public void sendFile() {
        putToFtpRunner.sendFile("D:\\repo\\com", "");
    }



    @Test
    public void list(){
        try {
            FTPFile[] ftpFiles = ftpConfig.getClient().listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                System.err.println(ftpFile.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}