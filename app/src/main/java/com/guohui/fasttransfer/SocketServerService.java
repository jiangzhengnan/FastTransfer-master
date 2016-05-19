package com.guohui.fasttransfer;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;


import com.guohui.fasttransfer.base.FileMessageList;
import com.guohui.fasttransfer.socket.FileTransferAsyncTask;
import com.guohui.fasttransfer.utils.AlertUtil;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class SocketServerService extends Service {
    public SocketServerService() {
    }


    //服务端socket
    ServerSocket serverSocket;


    //服务器线程
    Thread serverThread;


    @Override
    public void onCreate() {
        super.onCreate();
        serverThread = null;

        serverThread = new Thread() {

            @Override
            public void run() {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                serverSocket = null;
                try {
                    serverSocket = new ServerSocket(8889);
                    serverSocket.setReuseAddress(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (!isInterrupted()) {
                    try {
                        Socket currentClientSocket = serverSocket.accept();
                        currentClientSocket.setTcpNoDelay(true);
                        //callback when comes a connection
                        if (manager.getOnClientConnectedListener() != null) {
                            manager.getOnClientConnectedListener().OnClientConnected(currentClientSocket);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        interrupt();
                    }
                }
            }
        };
        serverThread.start();

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 唯一的manager管理类
     */
    public static SocketManager manager = new SocketManager();

    //套接字操作类
    public static class SocketManager {


        OnClientConnectedListener onClientConnectedListener = null;

        public void setOnClientConnectedListener(OnClientConnectedListener onClientConnectedListener) {
            this.onClientConnectedListener = onClientConnectedListener;
        }

        public OnClientConnectedListener getOnClientConnectedListener() {
            return onClientConnectedListener;
        }

        /**
         * 启动sercerTask
         *
         * @param context
         * @param files
         * @param isSender
         * @throws Exception
         */
        public FileTransferAsyncTask startServer(Context context, Socket socket, List<File> files, boolean isSender) throws Exception {
            if (files == null) {
                throw new Exception("file can't be null");
            } else if (Config.CONNECTED_OWNER_IP == null) {
                throw new Exception("Can't get owner ip, please check wifi p2p connection");
            }
            FileTransferAsyncTask serverTask = new FileTransferAsyncTask(context, socket, Config.CONNECTED_OWNER_IP);

            return serverTask;
        }

        /**
         * 发送文件
         *
         * @param context
         * @param files
         * @throws Exception
         */
        public FileTransferAsyncTask sendFile(Context context, Socket socket, List<File> files) throws Exception {
           return startServer(context, socket, files, true);

        }

        /**
         * 接收文件
         *
         * @param context
         * @throws Exception
         */
        public FileTransferAsyncTask reveiceFile(final Context context, Socket socket) throws Exception {
            if (Config.CONNECTED_OWNER_IP == null) {
                throw new Exception("Can't get owner ip, please check wifi p2p connection");
            }
            FileTransferAsyncTask serverTask = new FileTransferAsyncTask(context, socket, Config.CONNECTED_OWNER_IP);
            return serverTask;

        }


    }

    public interface OnClientConnectedListener {
        /**
         * 有客户端连接到了服务
         */
        public void OnClientConnected(Socket socket);
    }


//    class SocketListener extends Thread{
//        Socket socket;
//        SocketListener(Socket socket){
//            this.socket = socket;
//        }
//
//        boolean running = true;
//
//        public boolean isRunning() {
//            return running;
//        }
//
//        public void setRunning(boolean running) {
//            this.running = running;
//        }
//
//        public void stopListener(){
//            running = false;
//        }
//
//        @Override
//        public void run() {
//            while (running){
//                try {
//                    int result = socket.getInputStream().read();
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverThread.interrupt();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverSocket = null;
        serverThread = null;
        super.onDestroy();
    }
}
