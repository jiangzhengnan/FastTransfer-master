package com.guohui.fasttransfer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;

/**
 * Created by nangua on 2016/5/19.
 */
public class MediaUtil {
    /**
     * 得到本地所有的音乐文件
     * @param context
     * @return
     */
    public static ArrayList<File> getAllSongs(Context context) {
        ArrayList<File> songs = null;
          Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.DATA,
                       },
                null,
                null,
                null);
        songs = new ArrayList<File>();
        if (cursor.moveToFirst()) {

            File song = null;

            do {

                if (cursor.getString(0)!=null) {
                    song = new File(cursor.getString(0));
                    songs.add(song);
                }
            } while (cursor.moveToNext());

            cursor.close();
        }
        return songs;
    }

    /**
     * 得到本地图片文件
     * @param context
     * @return
     */
    public static ArrayList<HashMap<String,String>> getAllPictures(Context context) {
        ArrayList<HashMap<String,String>> picturemaps = new ArrayList<>();
        HashMap<String,String> picturemap;
        ContentResolver cr = context.getContentResolver();
        //先得到缩略图的URL和对应的图片id
        Cursor cursor = cr.query(
                Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        Thumbnails.IMAGE_ID,
                        Thumbnails.DATA
                },
                null,
                null,
                null);
        if (cursor.moveToFirst()) {
            do {
                picturemap = new HashMap<>();
                picturemap.put("image_id_path",cursor.getInt(0)+"");
                picturemap.put("thumbnail_path",cursor.getString(1));
                picturemaps.add(picturemap);
            } while (cursor.moveToNext());
            cursor.close();
        }
        //再得到正常图片的path
        for (int i = 0;i<picturemaps.size();i++) {
            picturemap = picturemaps.get(i);
            String media_id = picturemap.get("image_id_path");
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media.DATA
                    },
                    MediaStore.Audio.Media._ID+"="+media_id,
                    null,
                    null
            );
            if (cursor.moveToFirst()) {
                do {
                    picturemap.put("image_id",cursor.getString(0));
                    picturemaps.set(i,picturemap);
                } while (cursor.moveToNext());
                cursor.close();
            }
        }
        //图片总数为350
        //缩略图库总数为281
        return picturemaps;
    }

    /**
     * 得到本地所有的视频文件
     * @param context
     * @return
     */
    public static ArrayList<File> getAllVideos(Context context) {
        ArrayList<File> videos = null;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.Media.DATA,
                },
                null,
                null,
                null);
        videos = new ArrayList<File>();
        if (cursor.moveToFirst()) {
            File video = null;
            do {
                video = new File(cursor.getString(0));
                videos.add(video);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return videos;
    }
}
