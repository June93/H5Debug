
package com.mysoft.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * Use getFilesDir()
     *
     * @param context
     * @param fileName
     * @param byteArr
     * @throws IOException
     */
    public static void writeFileInInternalStorage(Context context, String fileName, byte[] byteArr)
        throws IOException {
        writeFile(context, context.getFilesDir(), fileName, byteArr);
    }

    /**
     * Use getFilesDir()
     *
     * @param context
     * @param fileName
     * @param byteArr
     * @throws IOException
     */
    public static void writeFileInCacheStorage(Context context, String fileName, byte[] byteArr)
        throws IOException {
        writeFile(context, context.getCacheDir(), fileName, byteArr);
    }

    private static void writeFile(Context context, File fileDir, String fileName, byte[] byteArr)
        throws IOException {
        File file = new File(fileDir, fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(byteArr);
        outputStream.close();
    }

    public static byte[] readFileFromInternalStorage(Context context, String fileName)
        throws IOException {
        return readFile(context, context.getFilesDir(), fileName);
    }

    public static byte[] readFileFromCacheStorage(Context context, String fileName)
        throws IOException {
        return readFile(context, context.getCacheDir(), fileName);
    }

    private static byte[] readFile(Context context, File dirName, String fileName)
        throws IOException {
        File file = new File(dirName, fileName);
        int size = (int)file.length();
        byte byteArr[] = new byte[size];

        FileInputStream inputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedInputStream.read(byteArr, 0, byteArr.length);
        bufferedInputStream.close();

        return byteArr;
    }

    public static void writeFileInExternalStorage(Context context, String fileName, byte byteArr[])
        throws IOException {
        if (context != null) {
            File externalDirectory = context.getExternalFilesDir(null);
            File file = new File(externalDirectory + "/" + fileName);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!file.exists()) {
                boolean b = file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArr);
            fileOutputStream.close();
        }
    }

    public static byte[] readFileFromExternalStorage(Context context, String fileName)
        throws IOException {
        byte[] byteArr;
        File file = new File(context.getExternalFilesDir(null) + "/" + fileName);
        int size = (int)file.length();
        byteArr = new byte[size];
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        bufferedInputStream.read(byteArr, 0, byteArr.length);
        bufferedInputStream.close();
        return byteArr;
    }

    // 优先读外部存取
    public static byte[] readFileFromStorage(Context context, String fileName) {
        byte[] byteArr = null;
        try {
            if (isExternalStorageReadable()) {
                byteArr = readFileFromExternalStorage(context, fileName);
            } else {
                byteArr = readFileFromInternalStorage(context, fileName);
            }

        } catch (Exception e) {
        }
        return byteArr;
    }

    // 优先往外部存取写，如果外部存取找不到或出现其它异常，则往内部存取写
    public static void writeFileInStorage(Context context, String fileName, byte byteArr[]) {
        try {
            if (isExternalStorageWritable()) {
                writeFileInExternalStorage(context, fileName, byteArr);
            } else {
                writeFileInInternalStorage(context, fileName, byteArr);
            }
        } catch (Exception e) {
        }
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED) || state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getExternalStoragePath(Context context) {
        if (isExternalStorageReadable()) {
            File file = context.getExternalFilesDir(null);
            if (file != null) {
                return file.toString();
            } else {
                return StringUtils.BLANK_STRING;
            }
        } else {
            return StringUtils.BLANK_STRING;
        }
    }

    // 优先取外部
    public static String getStoragePath(Context context) {
        String path = "";
        path = getExternalStoragePath(context);
        // 外部可读也有可能返回空，如果确实是外部路径不可用了才去尝试读内部路径
        if (TextUtils.isEmpty(path) && !isExternalStorageReadable()) {
            path = getInternalStoragePath(context);
        }
        return StringUtils.getNoneNullString(path);
    }

    public static String getInternalStoragePath(Context context) {
        return context.getFilesDir().toString();
    }

    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(File file) {
        boolean result = false;
        try {
            if (file != null && file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files.length >= 1) {
                        for (int i = 0; i < files.length; i++) {
                            deleteFile(files[i]);
                        }
                    }
                    boolean ch = file.delete();
                } else {
                    boolean ch = file.delete();
                }
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;

    }
    
    /*
     * Zip 解压缩方法
     */

    /**
     * 解压到指定目录
     *
     * @param zipPath
     * @param descDir
     * @author isea533
     */
    public static void unZipFiles(String zipPath, String descDir)
        throws IOException {
        unZipFiles(new File(zipPath), descDir);
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile
     * @param descDir
     * @author isea533
     */
    @SuppressWarnings("rawtypes")
    public static void unZipFiles(File zipFile, String descDir)
        throws IOException {
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry)entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
            // 压缩的文件格式中可能出现了"\"符号 zipEntryName可能以“\”开头
            outPath = outPath.replaceAll("\\\\", "");
            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;

            try {
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
            } catch (IOException e) {
                // 大小异常不处理
            }

            in.close();
            out.close();
        }
    }

    public static boolean isFile(String filePath) {
        if (filePath != null) {
            if (new File(filePath).isFile()) {
                return true;
            }
        }
        return false;
    }

    public static void checkDirectory(File dir) {
        if (dir.exists()) {
            if (!dir.isDirectory() && !dir.delete() && !dir.mkdirs()) {
                throw new RuntimeException("create file(" + dir + ") fail.");
            }
        } else if (!dir.mkdirs()) {
            throw new RuntimeException("create file(" + dir + ") fail.");
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    @SuppressWarnings("resource")
    public static long getFileSizes(File f)
        throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        } else {
            f.createNewFile();
        }
        return s;
    }

    public static long getFileSize(File f)
        throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    public static String FormetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#");
        String fileSizeString = "";
        if (fileS < 1048576) {
            fileSizeString = df.format((double)fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double)fileS / 1048576) + "M";
        }
        return fileSizeString;
    }

    public static String FormetFileSizeToM(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        fileSizeString = df.format((double)fileS / 1048576) + "M";
        return fileSizeString;
    }

    public static long getlist(File f) {// 递归求取目录文件个数
        long size = 0;
        File flist[] = f.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
    }

    public static boolean isLegalPath(String path) {
        boolean result = false;
        if (!TextUtils.isEmpty(path)) {
            int lastDotPosition = path.lastIndexOf(".");
            int lastSeparatorPosition = path.lastIndexOf(File.separator);
            if (lastDotPosition > lastSeparatorPosition) {
                result = true;
            }
        }
        return result;
    }

}
