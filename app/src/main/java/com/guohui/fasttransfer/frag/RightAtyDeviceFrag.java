package com.guohui.fasttransfer.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.utils.DBUtils;

import java.util.ArrayList;

/**
 * Created by nangua on 2016/5/13.
 */
public class RightAtyDeviceFrag extends Fragment {
    ArrayList<String> filenames;
    TextView device_show;
    String result = " ";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rightfrag_device_layout,container,false);
        initView(v);
        return v;
    }

    private void initView(View v) {

        device_show = (TextView) v.findViewById(R.id.device_show);
        filenames = new ArrayList<>();
        DBUtils utils = new DBUtils(getContext(),"Device");
        filenames = utils.queryDevice();
        if (filenames.size()!=0) {
            for (int i = 0;i<filenames.size();i++) {
                result+=filenames.get(i);
            }
        }
        device_show.setText(result);
    }
}
