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
public class SendAtyMusicFrag extends Fragment{
    private ArrayList<File> allsongs;
    private ListView sendfrag_music_lv;
    private TextView showmusic;
    //文件适配器
    private SendAtyFileFragRvAdapter sendAtyFileFragRvAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sendfrag_music_layout,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        showmusic = (TextView) v.findViewById(R.id.showmusic);
        allsongs = MediaUtil.getAllSongs(getContext());
        if (allsongs.size()!=0) {
            showmusic.setVisibility(View.GONE);
        }
        sendfrag_music_lv = (ListView) v.findViewById(R.id.sendfrag_music_lv);
        sendAtyFileFragRvAdapter = new SendAtyFileFragRvAdapter(getContext(),allsongs);
        sendfrag_music_lv.setAdapter(sendAtyFileFragRvAdapter);

    }
}
