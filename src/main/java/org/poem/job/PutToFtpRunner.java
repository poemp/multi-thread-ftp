package org.poem.job;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.util.Properties;

/**
 * 上传到文件
 */
public class PutToFtpRunner {

    private static final Logger log = LoggerFactory.getLogger(PutToFtpRunner.class);

    public static void sendFile(Properties properties) {
        String url = properties.getProperty("ftp.url");
        String port = properties.getProperty("ftp.port");
        String user = properties.getProperty("ftp.user");
        String password = properties.getProperty("ftp.password");
        String path = properties.getProperty("ftp.path");
        String tmpDir = properties.getProperty("ftp.tmpdir");
        String fileType = properties.getProperty("ftp.fileType");
        String sourceTable = properties.getProperty("sourceTable");
        String isFullSync = properties.getProperty("isFullSync");
        String dataSyncDay = properties.getProperty("dataSyncDay").replaceAll("-", "");
        String dataSplit = properties.getProperty("ftp.statSplit");
        String fileName = properties.getProperty("ftp.fileName");
        String tableName = sourceTable.substring(sourceTable.lastIndexOf(".") + 1);
        if ("TEXT".equals(fileType.toUpperCase())) {
            fileType = "txt";
        }
        FTPClient ftp = new FTPClient();
        InputStream local = null;
        try {
            //连接ftp服务器
            ftp.connect(url, Integer.parseInt(port));
            //登录
            ftp.login(user, password);
            //检查上传路径是否存在 如果不存在返回false
            boolean flag = ftp.changeWorkingDirectory(path);
            if (!flag) {
                //创建上传的路径  该方法只能创建一级目录，在这里如果/home/ftpuser存在则可创建image
                ftp.makeDirectory(path);
            }
            //指定上传路径
            ftp.changeWorkingDirectory(path);
            //指定上传文件的类型  二进制文件
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.sendSiteCommand("umask 02");

            //读取本地文件
            // tableName 要加 schema, 否则 ftp 发送失败
            File file = new File(tmpDir, sourceTable);

            // 设置权限 777
            boolean permissionReadFlag = file.setReadable(true, false);
            boolean permissionWritableFlag = file.setWritable(true, false);
            boolean permissionExecutableFlag = file.setExecutable(true, false);
            log.info("setPermission: permissionReadFlag = " + permissionReadFlag +
                    ", permissionWritableFlag = " + permissionWritableFlag +
                    ", permissionExecutableFlag = " + permissionExecutableFlag);
            if (!permissionReadFlag || !permissionWritableFlag || !permissionExecutableFlag) {
                log.warn("设置权限有问题，请检查");
            }

            File[] files = file.listFiles();
            log.info("文件 file.getAbsolutePath() = " + file.getAbsolutePath());
            if (files == null || files.length == 0) {
                return;
            }
            log.info("子文件数量 files.length=" + files.length);
            log.info("参数 fileName = " + fileName);
            if (fileName != null && !("".equals(fileName.trim()))) {
                tableName = fileName;
            }
            //table_name_账期时间.文件类型   tal_20170102.txt  tbl_201701.txt
            String ftpFileName = tableName + "_" + dataSyncDay;
            String statFileName = "dir." + tableName + "_" + dataSyncDay;
            if ("TRUE".equals(isFullSync.trim().toUpperCase())) {
                ftpFileName = tableName;
                statFileName = "dir." + tableName;
            }
            log.info("ftpFileName = " + ftpFileName + ", statFileName = " + statFileName);
            int num = 1;
            StringBuilder statBuilder = new StringBuilder();
            for (File childFile : files) {
                log.info("处理子文件: childFile = " + childFile.getAbsolutePath());
                if (childFile.getName().endsWith(fileType)) {
                    local = new FileInputStream(childFile);
                    //第一个参数是文件名
                    String format = new DecimalFormat("000").format(num);
                    String name = ftpFileName + "_" + format + "." + fileType;
                    log.info("上传子文件 local = " + childFile.getAbsolutePath() + " 到文件 name = " + name);
                    ftp.storeFile(name, local);
                    //数据文件名称、文件大小、文件条数、账期时间、数据文件生成时间
                    long fileLineNumber = getFileLineNumber(childFile);
                    String fileStat = getFileStat(name, childFile.length(), fileLineNumber,
                            dataSyncDay, new Date(System.currentTimeMillis()), dataSplit);
                    statBuilder.append(fileStat);
                    num++;
                }
                log.info("上传子文件成功，删除子文件 " + childFile.getAbsolutePath());
                boolean deleteFlag = childFile.delete();
                if (deleteFlag) {
                    log.info("删除子文件成功");
                } else {
                    log.warn("删除子文件失败");
                }
            }
            //发送校验文件
            log.info("上传校验文件到 statFileName = " + statFileName);
            InputStream stat = new ByteArrayInputStream(statBuilder.toString().getBytes());
            ftp.storeFile(statFileName, stat);
            log.info("删除本地文件 file = " + file.getAbsolutePath());
            boolean deleteFlag = file.delete();
            if (deleteFlag) {
                log.info("删除文件成功");
            } else {
                log.warn("删除文件失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭文件流
//                local.close();
                //退出
                ftp.logout();
                //断开连接
                ftp.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static long getFileLineNumber(File file) {
        LineNumberReader lnr = null;
        int lineNo = 0;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            lineNo = lnr.getLineNumber();
            log.info(String.format("fileNo is %d", lineNo));
            lnr.close();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return lineNo;
    }


    private static String getFileStat(String name, long length, long fileLineNumber, String fileName, Date date, String dataSplit) {
        return name +
                dataSplit +
                length +
                dataSplit +
                fileLineNumber +
                dataSplit +
                fileName +
                dataSplit +
                date + "\n";
    }
}
