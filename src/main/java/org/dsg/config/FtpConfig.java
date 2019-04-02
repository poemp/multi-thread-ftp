package org.dsg.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * @author
 * @since
 */
@Setter
@Getter
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "spring.client")
public class FtpConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpConfig.class);

    private FTPClient client = null;

    private String url;

    private Integer port;

    private String user;

    private String password;

    private String path;

    private String tmpdir;

    private String fileType;

    private String sourceTable;

    private String isFullSync;

    private String dataSyncDay;

    private String statSplit;

    private String fileName;

    private String excludeFields;

    private String excludePartitionFeild;

    private String dataSplit;

    private String fileDataCount;

    @Autowired
    private ApplicationContext applicationContext;



    @PostConstruct
    public void initClient() {
        LOGGER.info(" Init client Client");
        LOGGER.info(String.format("init hive databases - \n\t\turl:%s, " +
                "\n\t\tuserName:%s, " +
                "\n\t\tpassword:%s, ", url, user, password));
        if (client == null) {
            Assert.notNull(url, "url can\'t be null");
            Assert.notNull(port, "port can\'t be null");
            Assert.notNull(password, "password can\'t be null");
            try {
                client = new FTPClient();
                //连接ftp服务器
                client.connect(url, port);
                //登录
                client.login(user, password);
                //检查上传路径是否存在 如果不存在返回false
                boolean flag = client.changeWorkingDirectory(path);
                if (!flag) {
                    //创建上传的路径  该方法只能创建一级目录，在这里如果/home/ftpuser存在则可创建image
                    client.makeDirectory(path);
                }
                //指定上传路径
                client.changeWorkingDirectory(path);
                //指定上传文件的类型  二进制文件
                client.setFileType(FTP.BINARY_FILE_TYPE);
                client.sendSiteCommand("umask 02");
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
                SpringApplication.exit(applicationContext , () -> 0);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        LOGGER.info(" Close FTP Client. ");
        try {
            client.abort();
            client.logout();
            //断开连接
            client.disconnect();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }

    }
}
