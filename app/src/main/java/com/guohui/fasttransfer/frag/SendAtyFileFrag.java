package com.guohui.fasttransfer.frag;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.adapter.SendAtyFileFragAdapter;
import com.guohui.fasttransfer.aty.SendActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by nangua on 2016/5/13.
 */
public class SendAtyFileFrag extends Fragment {
    //主Rv
    public ListView filelv;
    //文件集合
    public ArrayList<File> files;
    //文件适配器
    public SendAtyFileFragAdapter sendAtyFileFragRvAdapter;
    //当前路径
    public String currnetPath;

    //根路径
    public String rootPath;

    //上一次返回键按下
    long lastBackPressed = 0;

    public ImageButton ibenSelected;

    //上一个位置
    public Stack<Integer> lastPostion;

    public TextView tvPath;
    public TextView sdrongliang;
    long alloflength = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sendfrag_file_layout, container, false);
 /*
        1. 加载数据
        2. 初始化监听器
        3. 初始化并设置Views
         */
        lastPostion = new Stack<>();
        loadFile();
        initAdapter();
        initViews(v);
        return v;
    }


    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(getContext(), blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    private String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return Formatter.formatFileSize(getContext(), blockSize * availableBlocks);
    }

    /**
     * 初始化Views
     */
    private void initViews(View v) {
        sdrongliang = (TextView) v.findViewById(R.id.sdrongliang);
        sdrongliang.setText(getSDAvailableSize() + "/" + getSDTotalSize());
        filelv = (ListView) v.findViewById(R.id.sendfrag_file_lv);
        tvPath = (TextView) v.findViewById(R.id.sendfrag_file_tvPath);
        tvPath.setText(currnetPath);
        filelv.setAdapter(sendAtyFileFragRvAdapter);
        filelv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前选中的文件
                File file = files.get(position);
                //如果是文件夹,点击后进入文件夹
                if (file.isDirectory()) {
                    currnetPath = file.getAbsolutePath();
                    Log.i("filePath", currnetPath);
                    //清空files
                    files.clear();
                    //将当前选中的目录里的元素添加到files里面
                    for (File tFile : file.listFiles()) {
                        files.add(tFile);
                    }
                    sendAtyFileFragRvAdapter.notifyDataSetChanged();
                    tvPath.setText(currnetPath);
                    lastPostion.push(filelv.getFirstVisiblePosition());
                } else {
                    try {
                        if (selectedFileMap.containsKey(position)) {
                            if (selectedFileMap.get(position)) {
                                SendActivity.instance.removeFromFiles(file.getAbsolutePath());
                                view.setBackgroundColor(0xffffffff);
                            } else {
                                SendActivity.instance.addToFiles(file.getAbsolutePath());
                                view.setBackgroundColor(0xaaffaa00);
                            }
                            selectedFileMap.put(position, !selectedFileMap.get(position));
                        } else {
                            selectedFileMap.put(position, true);
                            SendActivity.instance.addToFiles(file.getAbsolutePath());
                            view.setBackgroundColor(0xaaffaa00);
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "无法打开文件", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

  private   HashMap<Integer, Boolean> selectedFileMap = new HashMap<>();


    /**
     * 按下返回键
     * 返回键按下后判断当前目录是否是跟目录,如果不是,则返回上一个目录
     */
    public void sendonBackPressed() {
        //当前路径不是根路径

    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        sendAtyFileFragRvAdapter = new SendAtyFileFragAdapter(getContext(), files);
    }

    /**
     * 加载文件数据
     */
    private void loadFile() {
        files = new ArrayList<>();
        File sdPath;
            sdPath = Environment.getExternalStorageDirectory();
        //设置当前路径以及根路径均为sd卡的根目录
        currnetPath = sdPath.getAbsolutePath();
        rootPath = currnetPath;
        File[] tempFiles = sdPath.listFiles();
        if (tempFiles != null) {
            for (File file : tempFiles) {
                files.add(file);
                alloflength += file.length();
            }
        }

    }
    /**
     * SDCARD是否存
     */
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }



}
