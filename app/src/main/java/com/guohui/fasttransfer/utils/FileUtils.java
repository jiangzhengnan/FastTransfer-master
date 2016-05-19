package com.guohui.fasttransfer.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import com.guohui.fasttransfer.R;

import com.guohui.fasttransfer.Config;

import java.io.DataOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mac on 16/4/16.
 */
public class FileUtils {

    /**
     * 文件的图片
     *
     * @param file 文件
     * @return 图片id
     */
    public static int filterFileTypeImage(File file) {
        String type = getTailName(file.getName());
        if (file.isFile()) {
            if (type == null) {
                return R.drawable.file_empty;
            } else {
                type = type.toLowerCase();
                if (Config.TYPE_APK.contains(type)) {
                    return R.drawable.file_apk;
                } else if (Config.TYPE_CODE.contains(type)) {
                    return R.drawable.file_code;
                } else if (Config.TYPE_EXCEL.contains(type)) {
                    return R.drawable.file_excel;
                } else if (Config.TYPE_IMAGE.contains(type)) {
                    return R.drawable.file_picture;
                } else if (Config.TYPE_MUSIC.contains(type)) {
                    return R.drawable.file_music;
                } else if (Config.TYPE_PDF.contains(type)) {
                    return R.drawable.file_pdf;
                } else if (Config.TYPE_POWER_POINT.contains(type)) {
                    return R.drawable.file_powerpoint;
                } else if (Config.TYPE_TEXT.contains(type)) {
                    return R.drawable.file_text;
                } else if (Config.TYPE_VIDEO.contains(type)) {
                    return R.drawable.file_video;
                } else if (Config.TYPE_WORD.contains(type)) {
                    return R.drawable.file_word;
                } else if (Config.TYPE_ZIP.contains(type)) {
                    return R.drawable.file_zip;
                } else {
                    return R.drawable.file_empty;
                }
            }
        } else {
            return R.drawable.folder;
        }

    }

    /**
     * 　　* 获取存储卡的剩余容量，单位为字节
     * <p/>
     * 　　* @param filePath
     * <p/>
     * 　　* @return availableSpare
     */
    public static long getAvailableStore() {

        // 取得sdcard文件路径

        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

        // 获取block的SIZE

        long blocSize = statFs.getBlockSize();

        // 获取BLOCK数量

        // long totalBlocks = statFs.getBlockCount();

        // 可使用的Block的数量

        long availaBlock = statFs.getAvailableBlocks();

        // long total = totalBlocks * blocSize;

        long availableSpare = availaBlock * blocSize;

        return availableSpare;

    }

    public static String getTotalStorageSize() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

        return longToFileSize(statFs.getAvailableBytes());
    }


    private static String longToFileSize(long result) {
        long gb = 2 << 29;
        long mb = 2 << 19;
        long kb = 2 << 9;
        // return String.format("%.2fGB",result/gb);
        if (result < kb) {
            return result + "B";
        } else if (result >= kb && result < mb) {
            return String.format("%.2fKB", result / (double) kb);
        } else if (result >= mb && result < gb) {
            return String.format("%.2fMB", result / (double) mb);
        } else if (result >= gb) {
            return String.format("%.2fGB", result / (double) gb);
        }
        return null;
    }

    public static String generateSize(File file) {
        if (file.isFile()) {
            long result = file.length();
            return longToFileSize(result);
        }
        return null;
    }

    /**
     * 按照yyyy-MM-dd的格式格式化file的时间
     *
     * @param file
     * @return
     */
    public static String generateTime(File file) {
        Date date = new Date(file.lastModified());
        String result = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
        return result;

    }

    /**
     * 获取file的后缀名
     *
     * @param name
     * @return
     */
    public static String getTailName(String name) {
        String type = null;
        if (name.contains(".")) {
            String[] splits = name.split("\\.");
            // System.out.println(splits.length);
            type = splits[splits.length - 1].toLowerCase();
        }
        return type;
    }

    /**
     * 判断对话框
     *
     * @param context
     * @param title
     * @param message
     * @param okListener
     * @param cancleListener
     * @return
     */
    public static AlertDialog judgeAlertDialog(Context context, String title,
                                               String message, DialogInterface.OnClickListener okListener,
                                               DialogInterface.OnClickListener cancleListener) {
        AlertDialog aDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message)
                .setNegativeButton("确定", okListener)
                .setPositiveButton("取消", cancleListener).show();
        return aDialog;
    }

    //设置意图的类型
    public static String getIntentByFile(File file) {
        String type = getTailName(file.getName());
        if (file.isFile()) {
            if (type == null) {
                return "*/*";
            } else {
                type = type.toLowerCase();
                if (Config.TYPE_APK.contains(type)) {
                    return "application/*";
                } else if (Config.TYPE_CODE.contains(type)) {
                    return "text/*";
                } else if (Config.TYPE_EXCEL.contains(type)) {
                    return "application/msexcle";
                } else if (Config.TYPE_IMAGE.contains(type)) {
                    return "image/*";
                } else if (Config.TYPE_MUSIC.contains(type)) {
                    return "audio/*";
                } else if (Config.TYPE_PDF.contains(type)) {
                    return "application/pdf";
                } else if (Config.TYPE_POWER_POINT.contains(type)) {
                    return "application/vnd.ms-powerpoint";
                } else if (Config.TYPE_TEXT.contains(type)) {
                    return "text/*";
                } else if (Config.TYPE_VIDEO.contains(type)) {
                    return "video/*";
                } else if (Config.TYPE_WORD.contains(type)) {
                    return "application/msword";
                } else if (Config.TYPE_ZIP.contains(type)) {
                    return "application/x-zip-compressed";
                } else {
                    return "*/*";
                }
            }
        } else {
            return null;
        }
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }



}
