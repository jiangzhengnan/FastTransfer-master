package com.guohui.fasttransfer.aty;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.guohui.fasttransfer.R;
import com.slidingmenu.lib.SlidingMenu;

/**
 * Created by nangua on 2016/4/18 0018.
 */
public class MleftMenu extends SlidingMenu {
    private Context context;

    public MleftMenu(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        //设置SlidingMenu
        this.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        this.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        this.setShadowWidthRes(R.dimen.shadow_width);
        this.setShadowDrawable(R.drawable.shadow);
        //设置宽度
        this.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        //设置渐入渐出效果值
        this.setFadeDegree(0.35f);
        this.attachToActivity((Activity) context, SlidingMenu.SLIDING_CONTENT);
        //设置布局
        View view = LayoutInflater.from(context).inflate(R.layout.left_menu,null);
        LinearLayout top = (LinearLayout) view.findViewById(R.id.toplinearlayout);
        //设置占位view的高度为状态栏高度
        //或者可以设置padding
        top.setPadding(0,0,0,0);
        this.setMenu(R.layout.left_menu);
    }
}
