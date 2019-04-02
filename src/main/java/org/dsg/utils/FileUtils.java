package org.dsg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.LineNumberReader;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 列出文件夹下的文件
     *
     * @param path
     * @return
     */
    public static List<File> listFiles(String path,FilenameFilter filenameFilter) {
        File newFile = new File(path);
        if (newFile.exists()) {
            return listFiles(newFile,filenameFilter);
        }
        return null;
    }

    /**
     * @param file
     * @return
     */
    public static List<File> listFiles(File file, FilenameFilter filenameFilter) {
        List<File> fileList = new ArrayList<>();
        File[] files = file.listFiles(filenameFilter);
        if (files == null || files.length == 0) {
            return null;
        }
        for (File file1 : files) {
            if (file1.isDirectory()) {
                List<File> fs = listFiles(file1,filenameFilter);
                if (fs != null && fs.size() > 0) {
                    fileList.addAll(fs);
                }
            } else {
                fileList.add(file1);
            }
        }
        return fileList;
    }


    /**
     * 数据文件名称、文件大小、文件条数、账期时间、数据文件生成时间
     *
     * @param file
     * @return
     */
    public static long getFileLineNumber(File file) {
        LineNumberReader lnr = null;
        int lineNo = 0;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            lineNo = lnr.getLineNumber();
            lnr.close();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
        return lineNo;
    }


    /**
     * 校验文件
     *
     * @param name
     * @param length
     * @param fileLineNumber
     * @param fileName
     * @param date
     * @param dataSplit
     * @return
     */
    public static String getFileStat(String name, long length, long fileLineNumber, String fileName, Date date, String dataSplit) {
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


    /**
     * 删除文件
     *
     * @param path 删除的文件路径
     */
    public static void deleteFile(String path) {
        File newFile = new File(path);
        if (newFile.exists()) {
            deleteFile(newFile);
        }
    }

    /**
     * 删除文件
     *
     * @param file 删除的文件
     */
    public static void deleteFile(File file) {
//        try {
//            LOGGER.info(" Delete File: " + file.getAbsoluteFile());
//            boolean delete = Files.deleteIfExists(file.toPath());
//            if (delete) {
//                LOGGER.info("删除子文件成功");
//            } else {
//                LOGGER.warn("删除子文件失败");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
