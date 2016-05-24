package com.guohui.fasttransfer.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.adapter.SendAtyFileFragAdapter;
import com.guohui.fasttransfer.aty.SendActivity;
import com.guohui.fasttransfer.utils.MediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by nangua on 2016/5/13.
 */
public class SendAtyPictureFrag extends Fragment {
    private ArrayList<File> allpicture;
    private ListView sendfrag_picture_lv;
    private TextView showpicture;
    //文件适配器
     private SendAtyFileFragAdapter sendAtyFileFragRvAdapter;

    //保存已选文件的map
    private HashMap<Integer, Boolean> selectedFileMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sendfrag_picture_layout,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        showpicture = (TextView) v.findViewById(R.id.showpicture);
        allpicture = MediaUtil.getAllPictures(getContext());
        if (allpicture.size()!=0) {
            showpicture.setVisibility(View.GONE);
        }
        sendfrag_picture_lv = (ListView) v.findViewById(R.id.sendfrag_picture_lv);
        sendAtyFileFragRvAdapter = new SendAtyFileFragAdapter(getContext(),allpicture,0);
        sendfrag_picture_lv.setAdapter(sendAtyFileFragRvAdapter);
        sendfrag_picture_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前选中的文件
                File f = allpicture.get(position);
                if (selectedFileMap.containsKey(position)) {
                    if (selectedFileMap.get(position)) {
                        SendActivity.instance.removeFromFiles(f.getAbsolutePath());
                        view.setBackgroundColor(0xffffffff);
                    } else {
                        SendActivity.instance.addToFiles(f.getAbsolutePath());
                        view.setBackgroundColor(0xaaffaa00);
                    }
                    selectedFileMap.put(position, !selectedFileMap.get(position));
                } else {
                    selectedFileMap.put(position, true);
                    SendActivity.instance.addToFiles(f.getAbsolutePath());
                    view.setBackgroundColor(0xaaffaa00);
                }
            }
        });
    }
}
