package com.guohui.fasttransfer.aty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.guohui.fasttransfer.R;

public class MainActivity extends Activity {
    public static MainActivity mainActivity;
    private AnimationDrawable animationDrawable;
    private MyHomeFragButtonClickListner myHomeFragButtonClickListner;
    private ImageButton downarrow;
    //发送和接受按钮
    Button sendbtn;
    Button acceptbtn;

    ImageButton man_toobal_rightbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        //设置沉浸式标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
    }


    private void initView() {
        //设置下滑箭头的帧动画
        downarrow = (ImageButton) findViewById(R.id.downarrow);
        downarrow.setImageResource(R.drawable.down_arrow_animation);
        animationDrawable = (AnimationDrawable) downarrow.getDrawable();
        animationDrawable.start();
        //设置发送接受按钮的跳转方法
        myHomeFragButtonClickListner = new MyHomeFragButtonClickListner();
        sendbtn = (Button) findViewById(R.id.sendbtn);
        acceptbtn = (Button) findViewById(R.id.acceptbtn);
        man_toobal_rightbtn = (ImageButton) findViewById(R.id.man_toobal_rightbtn);
        sendbtn.setOnClickListener(myHomeFragButtonClickListner);
        acceptbtn.setOnClickListener(myHomeFragButtonClickListner);
        downarrow.setOnClickListener(myHomeFragButtonClickListner);
        man_toobal_rightbtn.setOnClickListener(myHomeFragButtonClickListner);
    }

    private class MyHomeFragButtonClickListner implements View.OnClickListener {
        Intent i ;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sendbtn:
                    i = new Intent(MainActivity.this,SendActivity.class);
                    MainActivity.mainActivity.overridePendingTransition(R.anim.initactivity_open, 0);
                    startActivity(i);
                    break;
                case R.id.acceptbtn:
                    i = new Intent(MainActivity.this,AcceptActivity.class);
                    MainActivity.mainActivity.overridePendingTransition(R.anim.initactivity_open, 0);

                    startActivity(i);
                    break;
                case R.id.downarrow:
                    i = new Intent(MainActivity.this,SendActivity.class);
                    MainActivity.mainActivity.overridePendingTransition(R.anim.initactivity_open, 0);
                    startActivity(i);
                    break;
                case R.id.man_toobal_rightbtn:
                    i = new Intent(MainActivity.this,RightActivity.class);
                    MainActivity.mainActivity.overridePendingTransition(R.anim.initactivity_open, 0);
                    startActivity(i);
                    break;
            }
        }
    }


}
