package org.dsg.job;

import org.dsg.config.FtpConfig;
import org.dsg.config.thread.FtpExecutorService;
import org.dsg.job.put.FilePutRunner;
import org.dsg.utils.FileUtils;
import org.dsg.utils.PermissionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * 上传到文件
 * @author Administrator
 */
@Service
public class PutToFtpRunner {

    private static final Logger log = LoggerFactory.getLogger(PutToFtpRunner.class);

    @Autowired
    private FtpConfig ftpConfig;

    @Autowired
    private FtpExecutorService ftpExecutorService;

    /**
     *
     * @param tmpDir
     * @param sourceTable
     */
    public void sendFile(String tmpDir, String sourceTable) {
        File file = new File(tmpDir, sourceTable);
        PermissionUtils.initPermission(file);
        List<File> files = FileUtils.listFiles(file, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File newFile = new File(dir+ File.separator + name);
                if (newFile.isDirectory()){
                    return true;
                }
                String fileType = ftpConfig.getFileType();
                String[] strings = fileType.split(",");
                for (String string : strings) {
                    if (name.endsWith(string)){
                        return true;
                    }
                }

                return false;
            }
        });
        if (files == null || files.size() == 0) {
            return;
        }

        long current = System.currentTimeMillis();
        for (File file1 : files) {
            ftpExecutorService.run(new FilePutRunner(ftpConfig, file1));
        }
        long c = System.currentTimeMillis();
        log.info("子文件数量 files.length=" + files.size());
        log.info("上传文件用时 :" + ((c - current)/ 1000) + "s"   );
        FileUtils.deleteFile(file);
    }

    /**
     * 保存数据
     */
    public void sendFile() {
        //读取本地文件
        // tableName 要加 schema, 否则 ftp 发送失败
        String tmpDir = ftpConfig.getTmpdir();
        String sourceTable = ftpConfig.getSourceTable();
        Assert.notNull(tmpDir, "tmpDir not null");
        Assert.notNull(sourceTable, "sourceTable not null");
        sendFile(tmpDir, sourceTable);
    }
}
