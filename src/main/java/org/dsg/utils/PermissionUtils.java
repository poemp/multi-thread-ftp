package org.dsg.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 初始化文件的权限
 */
public class PermissionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionUtils.class);

    /**
     * 初始化文件的权限
     * 777
     * @param file
     */
    public static void initPermission(File file) {
        // 设置权限 777
        boolean permissionReadFlag = file.setReadable(true, false);
        boolean permissionWritableFlag = file.setWritable(true, false);
        boolean permissionExecutableFlag = file.setExecutable(true, false);
        LOGGER.info("setPermission: permissionReadFlag = " + permissionReadFlag +
                ", permissionWritableFlag = " + permissionWritableFlag +
                ", permissionExecutableFlag = " + permissionExecutableFlag);
        if (!permissionReadFlag || !permissionWritableFlag || !permissionExecutableFlag) {
            LOGGER.warn("设置权限有问题，请检查");
        }
    }
}
