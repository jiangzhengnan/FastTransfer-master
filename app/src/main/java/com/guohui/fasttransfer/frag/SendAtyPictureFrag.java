package com.guohui.fasttransfer.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.adapter.SendAtyFileFragRvAdapter;
import com.guohui.fasttransfer.utils.MediaUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by nangua on 2016/5/13.
 */
public class SendAtyPictureFrag extends Fragment {
    private ArrayList<File> allpicture;
    private ListView sendfrag_picture_lv;
    private TextView showpicture;
    //文件适配器
     private SendAtyFileFragRvAdapter sendAtyFileFragRvAdapter;




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

        sendAtyFileFragRvAdapter = new SendAtyFileFragRvAdapter(getContext(),allpicture);
        sendfrag_picture_lv.setAdapter(sendAtyFileFragRvAdapter);

    }
}
