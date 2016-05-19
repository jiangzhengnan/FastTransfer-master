package com.guohui.fasttransfer.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.adapter.SendAtyFileFragRvAdapter;
import com.guohui.fasttransfer.utils.MediaUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nangua on 2016/5/13.
 */
public class SendAtyVideoFrag extends Fragment {
    private ArrayList<File> allvideos;
    private ListView sendfrag_video_lv;
    private TextView showvideo;
    //文件适配器
    private SendAtyFileFragRvAdapter sendAtyFileFragRvAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sendfrag_video_layout,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        showvideo = (TextView) v.findViewById(R.id.showvideo);
        allvideos = MediaUtil.getAllVideos(getContext());
        if (allvideos.size()!=0) {
            showvideo.setVisibility(View.GONE);
        }
        sendfrag_video_lv = (ListView) v.findViewById(R.id.sendfrag_video_lv);
        sendAtyFileFragRvAdapter = new SendAtyFileFragRvAdapter(getContext(),allvideos);
        sendfrag_video_lv.setAdapter(sendAtyFileFragRvAdapter);

    }
}
