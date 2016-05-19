package com.guohui.fasttransfer.socket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;


import com.guohui.fasttransfer.Config;
import com.guohui.fasttransfer.asynet.AsyNet;
import com.guohui.fasttransfer.base.FileMessageList;
import com.guohui.fasttransfer.base.FileMsg;
import com.guohui.fasttransfer.utils.AlertUtil;
import com.guohui.fasttransfer.utils.JsonExtendedUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;


/**
 * Created by Dikaros on 2016/5/11.
 */
public class FileTransferAsyncTask extends AsyNet<Object> {


    File dir = Config.getPersonalFile();
    Context context;
    boolean sendFileDevice = false;
    InetAddress serverIp;
    //接收套接字
    Socket socket;


    //传输请求接收
    boolean receiceCheck = false;


    public FileTransferAsyncTask(Context context) {
        this.context = context;
    }

    public FileTransferAsyncTask(Context context, Socket socket, InetAddress serverIp) {
        this(context);
        this.serverIp = serverIp;
        this.socket = socket;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    //同意传输
    public final static int ALLOW_TRANSFER = 200;

    //拒绝传输
    public final static int REJECT_TRANSFER = 500;

    //发送完成
    public final static int SEND_COMPLETE = 0x0;

    //接收完成
    public final static int RECEIVE_COMPLETE = 0xf;


    //发送多个文件
    List<File> sendFileList;


    public void setSendFileList(List<File> sendFileList) {
        this.sendFileList = sendFileList;
    }

    public List<File> getSendFileList() {
        return sendFileList;
    }

    @Override
    protected Object doInBackground(String... params) {
        Log.d("doInbackground", "开始尽心发送接收操作");

        try {
            final OutputStream os = socket.getOutputStream();
            final InputStream is = socket.getInputStream();
            //



                /*
                如果是发送文件方
                首先发送文件信息到接收端，接收端收到文件信息后确认并返回是否接收
                收到接收端的确认后开始发送文件
                 */
            if (sendFileDevice) {
                if (sendFileList==null||sendFileList.size()==0){
                    Log.d("doInbackground", "文件列表为空，取消操作");
                    return null;
                }
                Log.d("doInbackground", "发送操作");
                //1.发送文件信息到接收端
                FileMessageList messageList = new FileMessageList(sendFileList);
//                        FileMsg fileMsg = new FileMsg(file);
                String message = JsonExtendedUtil.generateJson(messageList);
                //获取输出流

                Log.d("doInbackground", "发送文件信息");

                //发送文件信息
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(message);
                writer.newLine();
                writer.flush();
                //2.等待接收信息
                //回复信息
                Log.d("doInbackground", "发送完成等待传回数据");

                //获取回复信息
                int replyMsg = is.read();
                switch (replyMsg) {
                    //接收方同意传输文件
                    case ALLOW_TRANSFER:
                        Log.d("doInbackground", "对方同意传输,开始传输");

                        FileInputStream fis = null;
                        for (int i = 0; i < sendFileList.size(); i++) {
                            File file = sendFileList.get(i);
                            //创建文件输入流读取文件信息
                            fis = new FileInputStream(file);
                            byte[] buff = new byte[1024];
                            int len = -1;
                            long sended = 0;
                            while ((len = fis.read(buff)) != -1) {
                                os.write(buff, 0, len);
                                sended += len;
                                //发送进度信息，以百分比的形式,第一个参数为百分比，第二个是文件位置
                                publishProgress(new Integer[]{(int) (sended * 100 / file.length()), i,sendFileList.size()});
                            }

                            os.flush();
                        }

                        AlertUtil.toastMess(context, "发送完成");
                        //发送文件传输完成帧
                        os.write(SEND_COMPLETE);
                        os.flush();
                        //接收文件成功的回应
                        int secondReply = is.read();
                        if (secondReply == RECEIVE_COMPLETE) {
                            AlertUtil.toastMess(context, "发送成功，已经收到对方确认");
                        }

                        //关闭文件输入流
                        fis.close();

                        break;
                    //接收方拒绝传输文件
                    case REJECT_TRANSFER:
                        is.close();
                        os.close();
                        return REJECT_TRANSFER;
                    default:
                        break;
                }
                is.close();
                os.close();


            }
            //如果是接收文件方
            else {
                Log.d("doInbackground", "接收操作");

                //1.启动socket连接
//                    Socket socket = new Socket();
//                    socket.bind(null);
//                    socket.connect(new InetSocketAddress(serverIp,8888),3000);
                //2.获取文件信息,并确认
                Log.d("doInbackground", "等待接收文件信息");

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                //文件json信息
                final String fileMsg = reader.readLine();
                final FileMessageList msgs = (FileMessageList) JsonExtendedUtil.compileJson(FileMessageList.class, fileMsg);
                Log.d("doInbackground", "收到对方传来的文件信息"+fileMsg);


                //阻塞等待用户选择对话框
                if (onReceivedListener==null){
                    return null;
                }
                if (onReceivedListener.onReceivedFileMessage(msgs)){
                    try {
                        //发送同意传输信息
                        Log.d("doInbackground", "发送同意接收文件请求");

                        os.write(ALLOW_TRANSFER);
                        Log.d("doInbackground", "开始接收文件");

                        //创建一个新的文件输出流
                        FileOutputStream fos = null;
                        for (int i = 0; i < msgs.getCount(); i++) {
                            FileMsg msg = msgs.getMsgs().get(i);
                            fos = new FileOutputStream(dir.getAbsolutePath() + "/" + msg.getName());
                            int len = -1;
                            byte[] buff = new byte[1024];
                            long received = 0;
                            while ((len=is.read(buff)) != -1) {
                                fos.write(buff, 0, len);
                                received += len;
                                //发送进度
                                publishProgress(new Integer[]{(int) (received * 100 / msg.getLength()), i,msgs.getCount()});
                            }
                            fos.flush();
                        }

                        Log.d("doInbackground", "文件接收完成");


                        int receiveFinish = is.read();
                        if (receiveFinish == SEND_COMPLETE) {
                            AlertUtil.toastMess(context, "对方发送完成");
                        }
                        //接收完成
                        os.write(RECEIVE_COMPLETE);
                        os.flush();

                        fos.close();
                        os.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        cancel(true);
                    }
                }else {
                    try {
                        os.write(REJECT_TRANSFER);
                    } catch (IOException e) {
                        e.printStackTrace();
                        cancel(true);
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
            cancel(true);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;

    }

    OnReceivedListener onReceivedListener;

    public void setOnReceivedListener(OnReceivedListener onReceivedListener) {
        this.onReceivedListener = onReceivedListener;
    }

    /**
     * 接收文件信息监听器
     */
    public interface OnReceivedListener{
        public boolean onReceivedFileMessage(FileMessageList msgs);
    }

    public static String generateSize(long result) {
        long gb = 2 << 29;
        long mb = 2 << 19;
        long kb = 2 << 9;
        // return String.format("%.2fGB",result/gb);
        if (result < kb) {
            return result + "B";
        } else if (result >= kb && result < mb) {
            return String.format("%.2fKB", result / (double) kb);
        } else if (result >= mb && result < gb) {
            return String.format("%.2fMB", result / (double) mb);
        } else if (result >= gb) {
            return String.format("%.2fGB", result / (double) gb);
        }
        return null;
    }


    public void execute(List<File> files, boolean sendFileDevice) {
        this.sendFileList = files;
        this.sendFileDevice = sendFileDevice;
        super.execute();
    }

    public void execute(boolean sendFileDevice) {
        this.sendFileDevice = sendFileDevice;
        super.execute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }
}
