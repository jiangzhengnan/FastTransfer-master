package com.guohui.fasttransfer.aty;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.frag.RightAtyDeviceFrag;
import com.guohui.fasttransfer.frag.RightAtyFileFrag;
import com.guohui.fasttransfer.frag.RightAtyHistoryFrag;
import com.guohui.fasttransfer.view.HorizontalScrollCursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nangua on 2016/4/13 0013.
 */
public class RightActivity extends FragmentActivity implements HorizontalScrollCursor.onViewPagerChanggedListner{
    //Viewpager
    private ViewPager rightvp;
    //页面切换监听
    private ViewPager.OnPageChangeListener onPageChangeListener;
    // 主界面适配器
    private FragmentPagerAdapter fPagerAdapter;
    // 所有的Tab
    private List<View> views;
    // 碎片每个碎片为一个布局
    private ArrayList<Fragment> fragments;

    //三个radiobtn按钮id
    private int[] radiobtnid = {R.id.right_rdbtn1, R.id.right_rdbtn2,
            R.id.right_rdbtn3};
    //三个radiobtn按钮
    private RadioButton[] radiobtns;

    //游标设计
    private HorizontalScrollCursor right_cursor;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rightaty_layout);
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
        //游标
        right_cursor = (HorizontalScrollCursor) findViewById(R.id.right_cursor);
        //初始化Viewpager
        rightvp = (ViewPager) findViewById(R.id.right_vp);
        radiobtns = new RadioButton[3];
        for (int i = 0; i < 3; i++) {
            final int j = i;
            radiobtns[i] = (RadioButton) findViewById(radiobtnid[i]);
            radiobtns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTagView(j);
                }
            });
        }
        // 创建碎片集合
        fragments = new ArrayList<Fragment>();
        LayoutInflater inflater = LayoutInflater.from(this);
        // 添加滑动
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.rightfrag_device_layout, null));
        views.add(inflater.inflate(R.layout.rightfrag_file_layout, null));
        views.add(inflater.inflate(R.layout.rightfrag_history_layout, null));
        fPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragments.size();
            }
            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        };
        // 声明各个Tab的实例
        RightAtyDeviceFrag rightAtyDeviceFrag = new RightAtyDeviceFrag();
        RightAtyFileFrag rightAtyFileFrag = new RightAtyFileFrag();
        RightAtyHistoryFrag rightAtyHistoryFrag = new RightAtyHistoryFrag();
        fragments.add(rightAtyDeviceFrag);
        fragments.add(rightAtyFileFrag);
        fragments.add(rightAtyHistoryFrag);
        rightvp.setAdapter(fPagerAdapter);

   /*     //初始化监听器
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            Animation animation = null;
            int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
            int two = one * 2;// 页卡1 -> 页卡3 偏移量
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                  *//*  animation = new TranslateAnimation(0,positionOffset,0,0);
                    animation.setFillAfter(true);// True:图片停在动画结束位置
                    animation.setDuration(1);
                    right_cursor.startAnimation(animation);*//*
               // right_cursor.scrollTo(positionOffsetPixels/3, (int) right_cursor.getY());
            }

            @Override
            public void onPageSelected(int position) {
                //改变radiobutton
                pageCheck(position);

                 switch (position) {
                    case 0:
                        if (currIndex == 1) {
                            animation = new TranslateAnimation(one, 0, 0, 0);
                        } else if (currIndex == 2) {
                            animation = new TranslateAnimation(two, 0, 0, 0);
                        }
                        break;
                    case 1:
                        if (currIndex == 0) {
                            animation = new TranslateAnimation(offset, one, 0, 0);
                        } else if (currIndex == 2) {
                            animation = new TranslateAnimation(two, one, 0, 0);
                        }
                        break;
                    case 2:
                        if (currIndex == 0) {
                            animation = new TranslateAnimation(offset, two, 0, 0);
                        } else if (currIndex == 1) {
                            animation = new TranslateAnimation(one, two, 0, 0);
                        }
                        break;
                }
                currIndex = position;
                animation.setFillAfter(true);// True:图片停在动画结束位置
                animation.setDuration(230);
                right_cursor.startAnimation(animation);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };*/
        right_cursor.setspace(30);
        right_cursor.setViewPager(rightvp);
        right_cursor.setcallback(this);
        //注意，设置Page 即缓存页面的个数，数过小时会出现fragment重复加载的问题
        rightvp.setOffscreenPageLimit(3);
    }



    // 更换标签
    private void changeTagView(int change) {
        rightvp.setCurrentItem(change, false);
    }

    /**
     * 自定义按钮选择的方法
     * @param position
     */

    @Override
    public void CheckPage(int position) {
        radiobtns[position].setChecked(true);
        for(int i = 0;i<3;i++) {
            if (i!=position) {
                radiobtns[i].setChecked(false);
            }
        }
    }
}
