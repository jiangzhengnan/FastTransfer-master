package com.guohui.fasttransfer.aty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.guohui.fasttransfer.Config;
import com.guohui.fasttransfer.R;
import com.guohui.fasttransfer.SocketServerService;
import com.guohui.fasttransfer.WiFiDirectBroadcastReceiver;
import com.guohui.fasttransfer.asynet.AsyNet;
import com.guohui.fasttransfer.base.FileMessageList;
import com.guohui.fasttransfer.base.FileMsg;
import com.guohui.fasttransfer.socket.FileTransferAsyncTask;
import com.guohui.fasttransfer.utils.AlertUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;


/**
 * Created by nangua on 2016/5/12.
 */
public class AcceptActivity extends Activity implements AsyNet.OnNetStateChangedListener {
    ArrayList<CharSequence> fileList;

    boolean isSender = false;

    //wifi p2p管理器
    private WifiP2pManager mManager;

    //wifi p2p通道
    private WifiP2pManager.Channel mChannel;
    //意图过滤器
    IntentFilter mIntentFilter;
    //广播接收器
    WiFiDirectBroadcastReceiver mReceiver;


    //socket binder
//    SocketServerService.SocketBinder serverBinder;


    public LinearLayout[] diviceNear;

    int[] ids = {R.id.device_0, R.id.device_1, R.id.device_2};



    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.accept_layout);
        isSender = getIntent().getBooleanExtra("isSender", false);
        if (isSender) {
            fileList = getIntent().getCharSequenceArrayListExtra("readyToSendFile");
        }
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        initView();
        dialog = new ProgressDialog(this);

        mManager.removeGroup(mChannel,null);
        //搜索周边设备
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                AlertUtil.toastMess(AcceptActivity.this, "加载成功");
            }

            @Override
            public void onFailure(int reason) {
                AlertUtil.toastMess(AcceptActivity.this, "加载失败" + reason);
            }
        });

    }


    /**
     * 连接到owner
     */
    public void connectToOwner() {
        new Thread() {
            boolean success = false;

            @Override
            public void run() {
                while (!success) {
                    try {
                        sleep(3000);
                        Socket clientSocket;
                        clientSocket = new Socket(Config.CONNECTED_OWNER_IP, 8889);
                        success = true;
                        serverConnected(clientSocket);
                    } catch (Exception e) {
                        e.printStackTrace();
                        success = false;
                    }
                }
            }
        }.start();
    }

    static List<FileMsg> receiveFileMsgs;
    static List<FileMsg> acceptFileMsgs;
    /**
     * 连接上了服务器
     *
     * @param socket
     */
    private void serverConnected(Socket socket) {
        FileTransferAsyncTask transferAsyncTask = new FileTransferAsyncTask(this, socket, Config.CONNECTED_OWNER_IP);
        if (isSender) {
            //设置并发送文件
            //发送文件
            List<File> files = new ArrayList<File>();
            for (CharSequence path : fileList) {
                files.add(new File((String) path));
            }
            transferAsyncTask.setSendFileList(files);
        }
        transferAsyncTask.setOnReceivedListener(
                new FileTransferAsyncTask.OnReceivedListener() {

            @Override
            public boolean onReceivedFileMessage(FileMessageList msgs) {
                receiveFileMsgs = msgs.getMsgs();


                Message message = new Message();
                message.what = 1;
                message.obj = msgs;
                handler.sendMessage(message);

                while (!flag){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }
        });
        transferAsyncTask.setOnNetStateChangedListener(AcceptActivity.this);
        transferAsyncTask.execute(isSender);
    }


    private void initView() {
        //设置沉浸式标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        diviceNear = new LinearLayout[3];
        for (int i = 0; i < diviceNear.length; i++) {
            final int j = i;
            diviceNear[i] = (LinearLayout) findViewById(ids[i]);
            diviceNear[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doConnectDevices(j);
                }
            });
            diviceNear[i].setVisibility(View.GONE);
        }
    }


    boolean result = false;
    //标志值，阻塞线程
    boolean flag = false;

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
        //如果处于前台状态，设置socket连接时的监听器
        SocketServerService.manager.setOnClientConnectedListener(new SocketServerService.OnClientConnectedListener() {
                                                 @Override
                                                 public void OnClientConnected(Socket socket) {
                                                     //如果是接收方
                                                     if (!isSender) {
                                                         try {
                                                             //启动接收器
                                                             FileTransferAsyncTask serverTask= SocketServerService.manager.reveiceFile(AcceptActivity.this, socket);
                                                             serverTask.setOnReceivedListener(new FileTransferAsyncTask.OnReceivedListener() {

                                                                 @Override
                                                                 public boolean onReceivedFileMessage(FileMessageList msgs) {

                                                                     Message message = new Message();
                                                                     message.what = 1;
                                                                     message.obj = msgs;
                                                                     handler.sendMessage(message);

                                                                     while (!flag){
                                                                         try {
                                                                             Thread.sleep(200);
                                                                         } catch (InterruptedException e) {
                                                                             e.printStackTrace();
                                                                         }
                                                                     }
                                                                     return result;
                                                                 }
                                                             });
                                                             serverTask.setOnNetStateChangedListener(AcceptActivity.this);
                                                             serverTask.execute(false);

                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     } else {
                                                         try {
                                                             //发送文件
                                                             List<File> files = new ArrayList<File>();
                                                             for (CharSequence path : fileList) {
                                                                 files.add(new File((String) path));
                                                             }
                                                             FileTransferAsyncTask task = SocketServerService.manager.sendFile(AcceptActivity.this, socket, files);
                                                             task.setOnNetStateChangedListener(AcceptActivity.this);
                                                             task.execute(files, isSender);
                                                         } catch (Exception e) {
                                                             e.printStackTrace();
                                                         }
                                                     }
                                                 }

                                             }

        );
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        SocketServerService.manager.setOnClientConnectedListener(null);
    }

    @Override
    protected void onDestroy() {
        //解绑服务
//        unbindService(connection);
        super.onDestroy();

    }

    android.os.Handler handler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    FileMessageList msgs = (FileMessageList) msg.obj;
                    AlertUtil.judgeAlertDialog(AcceptActivity.this, "接收提醒", msgs.getInfo(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result = true;
                            flag = true;
                            dialog.dismiss();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            result =false;
                            flag = true;
                            dialog.dismiss();
                        }
                    });
                    break;
                case PROGRESS_SHOW:
                    dialog.show();
                    break;
                case PROGRESS_DISMISS:
                    if (!isSender) {
                        //这里把receiveFileMsgs存到数据库里去










                    } else {
                        for (int i = 0;i<fileList.size();i++){
                            File tempfile = new File((String) fileList.get(i));
                            String filename = tempfile.getName();
                            //这里把文件名存到数据库里去












                        }
                    }
                    dialog.dismiss();
                    break;
                case PROGRESS_ON_PROGRESS:
                    TransProgressMsg m = (TransProgressMsg) msg.obj;
                    if (isSender){
                        dialog.setTitle("正在发送第"+m.getCurrentCount()+"个文件,共"+m.getTotalCount()+"个");

                    }else {
                        dialog.setTitle("正在接收第"+m.getCurrentCount()+"个文件,共"+m.getTotalCount()+"个");

                    }
                    dialog.setMessage("已完成"+m.getProgress()+"%");
                    dialog.setProgress(m.getProgress());
                    break;
            }
        }
    };


    public List<WifiP2pDevice> devices = new ArrayList<>();

    public void doConnectDevices(int i) {
        WifiP2pDevice device = devices.get(i);
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                AlertUtil.toastMess(AcceptActivity.this, "连接成功");
            }

            @Override
            public void onFailure(int reason) {
                AlertUtil.toastMess(AcceptActivity.this, "连接成功" + reason);
            }
        });
    }


    //传输进度对话框启动
    public static final int PROGRESS_SHOW = 2;
    //传输进度对话框消失
    public static final int PROGRESS_DISMISS = 3;
    //传输进度
    public static final int PROGRESS_ON_PROGRESS = 4;
    @Override
    public void beforeAccessNet() {
        handler.sendEmptyMessage(2);
    }

    @Override
    public void afterAccessNet(Object result) {
        handler.sendEmptyMessage(3);
    }

    @Override
    public void whenException() {
        handler.sendEmptyMessage(3);
    }

    @Override
    public void onProgress(Integer... progress) {
        TransProgressMsg transProgressMsg = new TransProgressMsg(progress);
        Message m  = new Message();
        m.what = 4;
        handler.sendMessage(m);

    }

    class TransProgressMsg{
        TransProgressMsg(Integer...pro){
            currentCount = pro[1];
            totalCount = pro[2];
            progress = pro[0];
        }
        int currentCount;
        int totalCount;
        int progress;

        public int getCurrentCount() {
            return currentCount;
        }

        public int getProgress() {
            return progress;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }
}
