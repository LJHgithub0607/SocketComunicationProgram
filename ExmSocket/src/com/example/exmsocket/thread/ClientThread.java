package com.example.exmsocket.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
  
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
  
public class ClientThread implements Runnable {
	private static final String TAG = "ClientThread";
	private static final String SOCKET_IP = "192.168.0.212";  // 模拟器使用
	//private static final String SOCKET_IP = "192.168.253.1";  // 真机使用
	private static final int SOCKET_PORT = 52000;
	public static String ACTION_RECV_MSG = "com.example.exmsocket.RECV_MSG";
	public static String ACTION_GET_LIST = "com.example.exmsocket.GET_LIST";
	public static String CONTENT = "CONTENT";
	public static String SPLIT_LINE = "|";
	public static String SPLIT_ITEM = ",";

	public static String LOGIN = "LOGIN";
	public static String LOGOUT = "LOGOUT";
	public static String SENDMSG = "SENDMSG";
	public static String RECVMSG = "RECVMSG";
	public static String GETLIST = "GETLIST";

    private Context mContext;
    private Socket mSocket;
    // 定义接收UI线程的Handler对象
    public Handler mRecvHandler;
    private BufferedReader mReader = null;
    private OutputStream mWriter = null;
    
    public ClientThread(Context context) {
    	mContext = context;
    }
  
    @Override  
    public void run() {
    	mSocket = new Socket();
        try {
        	mSocket.connect(new InetSocketAddress(SOCKET_IP, SOCKET_PORT), 3000);
            mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            mWriter = mSocket.getOutputStream();
            // 启动一条子线程来读取服务器相应的数据  
            new Thread() {
                @Override  
                public void run() {
                    String content = null;
                    try {
                        while ((content = mReader.readLine()) != null) {
                            // 读取到来自服务器的数据之后，发送消息通知
                        	ClientThread.this.notify(0, content);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ClientThread.this.notify(97, e.getMessage());
                    }
                }
            }.start();
            Looper.prepare();
            mRecvHandler = new Handler() {
                @Override  
                public void handleMessage(Message msg) {
					// 接收到UI线程的中用户输入的数据
					try {
						mWriter.write(msg.obj.toString().getBytes("utf8"));
					} catch (Exception e) {
						e.printStackTrace();
						ClientThread.this.notify(98, e.getMessage());
					}
                }
            };
            Looper.loop();
        } catch (Exception e) {
        	e.printStackTrace();
        	notify(99, e.getMessage());
        }
    }
    
    private void notify(int type, String message) {
    	if (type == 99) {
    		String content = String.format("%s%s%s%s", "ERROR", SPLIT_ITEM, SPLIT_LINE, message);
			Intent intent1 = new Intent(ACTION_RECV_MSG);
			intent1.putExtra(CONTENT, content);
			mContext.sendBroadcast(intent1);
			Intent intent2 = new Intent(ACTION_GET_LIST);
			intent2.putExtra(CONTENT, content);
			mContext.sendBroadcast(intent2);
    	} else {
			int pos = message.indexOf(SPLIT_LINE);
			String head = message.substring(0, pos-1);
			String[] splitArray = head.split(SPLIT_ITEM);
			String action = "";
			if (splitArray[0].equals(RECVMSG)) {
				action = ACTION_RECV_MSG;
			} else if (splitArray[0].equals(GETLIST)) {
				action = ACTION_GET_LIST;
			}
	    	Log.d(TAG, "action="+action+", message="+message);
			Intent intent = new Intent(action);
			intent.putExtra(CONTENT, message);
			mContext.sendBroadcast(intent);
    	}
    }
    
}
