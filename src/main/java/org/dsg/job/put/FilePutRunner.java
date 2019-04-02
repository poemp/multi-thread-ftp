package org.dsg.job.put;

import org.apache.commons.io.IOUtils;
import org.dsg.config.FtpConfig;
import org.dsg.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.sql.Date;
import java.util.concurrent.Callable;

/**
 * @author Administrator
 */
public class FilePutRunner implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(FilePutRunner.class);
    private static final String FULL_SYNC = "true";
    private FtpConfig ftpConfig;
    private File childFile;

    public FilePutRunner(FtpConfig ftpConfig, File childFile) {
        this.ftpConfig = ftpConfig;
        this.childFile = childFile;
    }


    /**
     * 上传数据
     *
     * @return
     */
    public String put() {
        String isFullSync = StringUtils.isEmpty(ftpConfig.getIsFullSync()) ? "" : ftpConfig.getIsFullSync();
        String dataSyncDay = StringUtils.isEmpty(ftpConfig.getDataSyncDay()) ? "" : ftpConfig.getDataSyncDay().replaceAll("-", "");
        String fileName = ftpConfig.getFileName();
        String sourceTable = ftpConfig.getSourceTable();
        String tableName = StringUtils.isEmpty(sourceTable) ? "" : sourceTable.substring(sourceTable.lastIndexOf(".") + 1);
        if (fileName != null && !("".equals(fileName.trim()))) {
            tableName = fileName;
        }
        //table_name_账期时间.文件类型   tal_20170102.txt  tbl_201701.txt
        String ftpFileName = tableName + "_" + dataSyncDay;
        String statFileName = "dir."+tableName+"_"+dataSyncDay;
        if (FULL_SYNC.equalsIgnoreCase(isFullSync.trim().toUpperCase())) {
            ftpFileName = tableName;
            statFileName = "dir."+tableName;
        }
        BufferedInputStream inputStream = null;

        try {
            String fileNames = childFile.getName();
            String ext = fileNames.substring(fileNames.lastIndexOf(".")+ 1);
            inputStream =  new BufferedInputStream(new FileInputStream(childFile));
            //第一个参数是文件名
            String format = String.valueOf(System.currentTimeMillis());
            String name = ftpFileName + "_" + format + "." + ext;
            boolean save = ftpConfig.getClient().storeFile(name, inputStream);
            logger.info(save + " : 上传子文件 local = " + childFile.getAbsolutePath() + " 到文件 name = " + name);
            //数据文件名称、文件大小、文件条数、账期时间、数据文件生成时间
            long fileLineNumber = FileUtils.getFileLineNumber(childFile);
            String fileStat = FileUtils.getFileStat(name,
                    childFile.length(),
                    fileLineNumber,
                    dataSyncDay,
                    new Date(System.currentTimeMillis()),
                    ftpConfig.getStatSplit());
            //发送校验文件
            InputStream stat = new ByteArrayInputStream(fileStat.getBytes());
            ftpConfig.getClient().storeFile(statFileName,stat);
            return fileStat;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    /**
     * @return
     * @throws Exception
     */
    @Override
    public Void call() throws Exception {
        try {
            put();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public FtpConfig getFtpConfig() {
        return ftpConfig;
    }

    public void setFtpConfig(FtpConfig ftpConfig) {
        this.ftpConfig = ftpConfig;
    }

    public File getChildFile() {
        return childFile;
    }

    public void setChildFile(File childFile) {
        this.childFile = childFile;
    }
}
