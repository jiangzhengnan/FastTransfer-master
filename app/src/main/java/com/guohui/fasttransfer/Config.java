package com.guohui.fasttransfer;

import android.os.Environment;

import java.io.File;
import java.net.InetAddress;

/**
 * Created by mac on 16/4/16.
 */
public class Config {
    /**
     *  视频格式
     */
    public static final String TYPE_VIDEO = "mp4,3gp,rm,avi,mov,wmv,rmvb,flv";
    /**
     * 音乐格式
     */
    public static final String TYPE_MUSIC = "mp3,wav,wma,ogg,ape,acc,flac";

    /**
     * 代码格式
     */
    public static final String TYPE_CODE = "java,c,m,cpp,cs,html,css,js,swift,asp,aspx,php,jsp,xml,h";

    /**
     * 图片格式
     */
    public static final String TYPE_IMAGE = "png,jpg,jpeg,gif,bmp,svg";
    /**
     * word格式
     */
    public static final String TYPE_WORD = "doc,docx";
    /**
     * ppt格式
     */
    public static final String TYPE_POWER_POINT = "ppt,pptx";
    /**
     * excel格式
     */
    public static final String TYPE_EXCEL = "xls,xlsx";
    /**
     * pdf格式
     */
    public static final String TYPE_PDF = "pdf";
    /**
     * 文本格式
     */
    public static final String TYPE_TEXT = "txt";
    /**
     * 压缩格式
     */
    public static final String TYPE_ZIP = "zip,rar,7z";
    /**
     * apk
     */
    public static final String TYPE_APK = "apk";


    public  static  String PERSONAL_FILE = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"FileTransfer";

    public static File getPersonalFile(){
        File file = new File(PERSONAL_FILE);
        if (!file.exists()){
            file.mkdirs();
        }
        return file;
    }

    public static InetAddress CONNECTED_OWNER_IP = null;

    public static P2pRole CURRENT_ROLE = P2pRole.NONE;
    public enum P2pRole{
        GROUP_OWNRR,GROUP_MEMBER,NONE
    }

    public static boolean isSenerDevice = false;
    public static String generateFileSize(long result) {
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
}
