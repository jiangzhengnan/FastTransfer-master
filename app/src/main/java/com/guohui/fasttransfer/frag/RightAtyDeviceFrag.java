package com.guohui.fasttransfer.frag;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guohui.fasttransfer.R;

/**
 * Created by nangua on 2016/5/13.
 */
public class RightAtyDeviceFrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.rightfrag_device_layout,container,false);

        return v;
    }
}
