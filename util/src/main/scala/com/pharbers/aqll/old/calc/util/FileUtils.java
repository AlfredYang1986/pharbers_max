package com.pharbers.aqll.old.calc.util;

import java.io.File;
/**
 * Created by liwei on 2017/4/19.
 */
// TODO : 创建新文件和目录
public class FileUtils {
    // TODO : 删除文件夹,param folderPath 文件夹完整绝对路径
    public static void delFolder(String folderPath) {
        try {
            // TODO : 删除完里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            // TODO : 删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO : 删除指定文件夹下所有文件,param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                // TODO : 先删除文件夹里面的文件
                delAllFile(path + "/" + tempList[i]);
                // TODO : 再删除空文件夹
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }
}
