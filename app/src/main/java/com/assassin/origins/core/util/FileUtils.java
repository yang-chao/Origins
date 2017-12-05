package com.assassin.origins.core.util;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.assassin.origins.BaseApplication;

public class FileUtils {
    private final static int BUFFER_SIZE = 1024;

    /**
     * 解压文件
     *
     * @param sourceZip
     * @param outFileName
     * @return
     * @throws IOException
     */
    public static boolean unZipFile(String sourceZip, String outFileName) {
        OutputStream os = null;
        InputStream is = null;
        try {
            ZipFile zfile = new ZipFile(sourceZip);
            Enumeration zList = zfile.entries();
            ZipEntry ze;
            byte[] buf = new byte[BUFFER_SIZE];
            int readLen;
            while (zList.hasMoreElements()) {
                // 从ZipFile中得到一个ZipEntry
                ze = (ZipEntry) zList.nextElement();
                if (ze.isDirectory()) {
                    continue;
                }
                // 以ZipEntry为参数得到一个InputStream，并写到OutputStream中
                os = new BufferedOutputStream(
                        new FileOutputStream(getRealFileName(outFileName,
                                ze.getName())));
                is = new BufferedInputStream(
                        zfile.getInputStream(ze));
                while ((readLen = is.read(buf, 0, BUFFER_SIZE)) != -1) {
                    os.write(buf, 0, readLen);
                }
                is.close();
                os.close();
            }
            zfile.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {

                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {

                }
            }
        }
        return true;

    }


    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        if (TextUtils.isEmpty(baseDir) || TextUtils.isEmpty(absFileName)) {
            return null;
        }
        String[] dirs = absFileName.split("/");

        File ret = new File(baseDir);

        if (dirs != null && dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                ret = new File(ret, dirs[i]);
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, dirs[dirs.length - 1]);
        return ret;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return deleteFile(new File(path));
    }

    /**
     * 递归删除文件
     *
     * @param file 要删除的文件
     */
    public static boolean deleteFile(File file) {
        return deleteFile(file, true);
    }

    /**
     * 递归删除文件
     *
     * @param file          要删除的文件
     * @param directorySelf True，删除第一级目录本身；False，不删除第一级目录
     */
    public static boolean deleteFile(File file, boolean directorySelf) {
        if (file != null && file.exists()) {
            try {
                if (file.isFile()) { // 是文件直接删除
                    file.delete();
                } else if (file.isDirectory()) { // 是文件夹的话递归删除文件夹下的文件
                    File files[] = file.listFiles();
                    if (files == null) {
                        return true; // 文件夹下没有子文件
                    }
                    for (int i = 0; i < files.length; i++) {
                        deleteFile(files[i]);
                    }
                }
                if (directorySelf) {
                    file.delete();
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 获取文件夹的大小
     *
     * @param dir 文件目录
     * @return
     */
    public static long dirSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }

    public static String getRealPathFromURI(Uri contentUri) {
        if (contentUri == null) {
            return "";
        }
        String res = null;
        String[] pro = {MediaStore.Images.Media.DATA};
        Cursor cursor = BaseApplication.getInstance().getContentResolver().query(contentUri, pro, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        if (cursor != null) {
            cursor.close();
        }
        return res;
    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

}
