package com.guohui.fasttransfer.aty;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.frag.SendAtyFileFrag;
import com.guohui.fasttransfer.frag.SendAtyMusicFrag;
import com.guohui.fasttransfer.frag.SendAtyPictureFrag;
import com.guohui.fasttransfer.frag.SendAtyVideoFrag;
import com.guohui.fasttransfer.view.HorizontalScrollCursor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by nangua on 2016/5/12.
 */
public class SendActivity extends FragmentActivity implements HorizontalScrollCursor.onViewPagerChanggedListner {
    //Viewpager
    ViewPager sendvp;
    //页面切换监听
    private ViewPager.OnPageChangeListener onPageChangeListener;
    // 主界面适配器
    FragmentPagerAdapter fPagerAdapter;
    // 所有的Tab
    private List<View> views;
    // 碎片每个碎片为一个布局
    private ArrayList<Fragment> fragments;

    //三个radiobtn按钮id
    private int[] radiobtnid = {R.id.send_rdbtn1, R.id.send_rdbtn2,
            R.id.send_rdbtn3,R.id.send_rdbtn4};
    //三个radiobtn按钮
    private RadioButton[] radiobtns;

    SendAtyFileFrag fileFrag;

    ArrayList<CharSequence> readyToSendFiles;

    int currentTag = 0;

    Button btnSelected;


    //游标设计
    HorizontalScrollCursor send_cursor;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    public static SendActivity instance;

    Button btnSendFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_layout);
        instance = this;
        //设置沉浸式标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        initView();

    }

    @Override
    protected void onDestroy() {
        instance = null;
        super.onDestroy();
    }




    int shangcideweizhi = 0;

    private void initView() {
        //游标
        send_cursor = (HorizontalScrollCursor) findViewById(R.id.send_cursor);
        btnSendFile = (Button) findViewById(R.id.btn_send_file);
        readyToSendFiles = new ArrayList<>();
        btnSelected = (Button) findViewById(R.id.btn_selected);
        btnSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送文件
                Intent intent = new Intent(SendActivity.this,AcceptActivity.class);
                intent.putExtra("isSender",true);
//                intent.putSExtra("readyToSendFile",readyToSendFiles);
                intent.putCharSequenceArrayListExtra("readyToSendFile",readyToSendFiles);
                startActivity(intent);
            }

        });
        //初始化动画
       // InitImageView();
        //初始化Viewpager
        sendvp = (ViewPager) findViewById(R.id.send_vp);
        radiobtns = new RadioButton[4];
        for (int i = 0; i < 4; i++) {
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
        views.add(inflater.inflate(R.layout.sendfrag_file_layout, null));
        views.add(inflater.inflate(R.layout.sendfrag_music_layout, null));
        views.add(inflater.inflate(R.layout.sendfrag_picture_layout, null));
        views.add(inflater.inflate(R.layout.sendfrag_video_layout, null));
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
        fileFrag = new SendAtyFileFrag();
        SendAtyPictureFrag pictureFrag = new SendAtyPictureFrag();
        SendAtyMusicFrag musicFrag = new SendAtyMusicFrag();
        SendAtyVideoFrag  videoFrag = new SendAtyVideoFrag();
        fragments.add(fileFrag);
        fragments.add(pictureFrag);
        fragments.add(musicFrag);
        fragments.add(videoFrag);
        sendvp.setAdapter(fPagerAdapter);


  /*      //初始化监听器
        onPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                 //改变radiobutton
                 pageCheck(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };*/

        //sendvp.setOnPageChangeListener(onPageChangeListener);
        //注意，设置Page 即缓存页面的个数，数过小时会出现fragment重复加载的问题
        sendvp.setOffscreenPageLimit(4);
        send_cursor.setspace(30);
        send_cursor.setViewPager(sendvp);
        send_cursor.setcallback(this);
    }

    /**
     * 初始化动画
     */
/*    private void InitImageView() {
        cursor = (ImageView) findViewById(R.id.iv_cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
          screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }*/

    int screenW;
    // 更换标签
    private void changeTagView(int change) {
        sendvp.setCurrentItem(change, false);
    }


    @Override
    public void onBackPressed() {
        if (currentTag!=0){
            super.onBackPressed();
            return;
        }
        if (fileFrag==null){
            return;
        }
        if (!fileFrag.rootPath.equals(fileFrag.currnetPath)) {
            //当前文件夹
            File file = new File(fileFrag.currnetPath);
            File pFile = file.getParentFile();
            fileFrag. currnetPath = pFile.getAbsolutePath();
            fileFrag.files.clear();
            //将当前选中的目录里的元素添加到files里面
            for (File tFile : pFile.listFiles()) {
                fileFrag.files.add(tFile);
            }
            fileFrag.sendAtyFileFragRvAdapter.notifyDataSetChanged();
            fileFrag.tvPath.setText(fileFrag.currnetPath);
            fileFrag.filelv.smoothScrollToPosition(fileFrag.lastPostion.pop());
        } else {
            //当前时间
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastBackPressed < 2000) {
//                //   super.onBackPressed();
//            } else {
//                Toast.makeText(getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
//            }
//            lastBackPressed = currentTime;
            super.onBackPressed();
        }
    }

    public void addToFiles(String path){
        if (!readyToSendFiles.contains(path)){
            readyToSendFiles.add(path);
            btnSelected.setText("已选(" + readyToSendFiles.size() + ")");
        }
    }

    public void removeFromFiles(String path){
        if (readyToSendFiles.contains(path)){
            readyToSendFiles.remove(path);
            btnSelected.setText("已选("+readyToSendFiles.size()+")");
        }
    }

     public  void  onBack(View v){
         super.onBackPressed();
     }

    @Override
    public void CheckPage(int position) {
        radiobtns[position].setChecked(true);
        for(int i = 0;i<4;i++) {
            if (i!=position) {
                radiobtns[i].setChecked(false);
            }
        }
    }
}
