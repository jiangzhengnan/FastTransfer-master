package com.guohui.fasttransfer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.guohui.fasttransfer.aty.AcceptActivity;
import com.guohui.fasttransfer.aty.MainActivity;
import com.guohui.fasttransfer.utils.AlertUtil;

import java.net.InetAddress;
import java.util.List;

/**
 * wifi Direct监听器
 * Created by Dikaros on 2016/5/10.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private AcceptActivity mActivity;

    boolean isOwner = false;

    InetAddress  groupIp;



    WifiP2pManager.PeerListListener mPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
//            mActivity.adapter.clear();
            mActivity.devices.clear();
            for (int i=0;i<mActivity.diviceNear.length;i++){
                mActivity.diviceNear[i].setVisibility(View.GONE);
            }


            int i = 0;
            for (WifiP2pDevice p2pDevice : peers.getDeviceList()) {
                if (i>2){
                    break;
                }
                mActivity.devices.add(p2pDevice);
                mActivity.diviceNear[i].setVisibility(View.VISIBLE);
                i++;

            }

            //list扩充

            Config.CURRENT_ROLE = Config.P2pRole.NONE;
        }
    };

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       AcceptActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }




    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // Check to see if Wi-Fi is enabled and notify appropriate activity
            //检测wifi是否可用，并将检测结果通知到此处
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                AlertUtil.toastMess(context,"wifi p2p可用");
            } else {
                // Wi-Fi P2P is not enabled
                AlertUtil.toastMess(context,"wifi p2p不可用");
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // Call WifiP2pManager.requestPeers() to get a list of current peers
            //调用WifiP2pManager.requestPeers方法获取成功连接的设备列表，全部结果出来时调用
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Respond to new connection or disconnections
            //响应wifi连接状态

            if (mManager == null)
                return;

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {

                // We are connected with the other device, request
                // connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        // 这里可以查看变化后的网络信息

                        // 通过传递进来的WifiP2pInfo参数获取变化后的地址信息
                        InetAddress groupOwnerAddress = info.groupOwnerAddress;
                        groupIp = groupOwnerAddress;
                        Config.CONNECTED_OWNER_IP  = groupOwnerAddress;
                        // 通过协商，决定一个小组的组长
                        if (info.groupFormed && info.isGroupOwner) {
                            // 这里执行P2P小组组长的任务。
                            // 通常是创建一个服务线程来监听客户端的请求
                            AlertUtil.toastMess(context,"我是组长"+groupOwnerAddress);
                            //组长建立ServerSocket
                            isOwner = true;
                            Config.CURRENT_ROLE = Config.P2pRole.GROUP_OWNRR;


                        } else if (info.groupFormed&&!info.isGroupOwner) {
                            // 这里执行普通组员的任务
                            // 通常是创建一个客户端向组长的服务器发送请求
                            AlertUtil.toastMess(context,"我是组员");
                            //组员建立socket
                            isOwner = false;
                            Config.CURRENT_ROLE = Config.P2pRole.GROUP_MEMBER;
                            //普通组员创建一个socket连接到主机
                            mActivity.connectToOwner();
                        }
                    }
                });
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            // Respond to this device's wifi state changing
            //响应设备的Wifi状态变化
        }
    }


}
