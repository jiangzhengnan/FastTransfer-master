package com.guohui.fasttransfer.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

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
                        MediaStore.Audio.Media.DISPLAY_NAME,
                       },
                null,
                null,
                null);
        songs = new ArrayList<File>();
        if (cursor.moveToFirst()) {

            File song = null;

            do {
                song = new File(cursor.getString(0));
                songs.add(song);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return songs;
    }

    /**
     * 得到本地所有的图片文件
     * @param context
     * @return
     */
    public static ArrayList<File> getAllPictures(Context context) {
        ArrayList<File> pictures = null;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.Media.DISPLAY_NAME,
                },
                null,
                null,
                null);
        pictures = new ArrayList<File>();
        if (cursor.moveToFirst()) {
            File picture = null;
            do {
                picture = new File(cursor.getString(0));
                pictures.add(picture);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return pictures;
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
                        MediaStore.Video.Media.DISPLAY_NAME,
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
