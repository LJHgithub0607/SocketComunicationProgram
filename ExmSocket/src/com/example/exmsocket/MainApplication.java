package com.example.exmsocket;

import com.example.exmsocket.thread.ClientThread;
import com.example.exmsocket.util.DateUtil;

import android.app.Application;
import android.os.Build;
import android.os.Message;
import android.util.Log;

public class MainApplication extends Application {
	private static final String TAG = "MainApplication";

	private static MainApplication mApp;
	private String mNickName;
	private ClientThread mClientThread;

	public static MainApplication getInstance() {
		return mApp;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApp = this;
		mClientThread = new ClientThread(mApp);
		new Thread(mClientThread).start();
	}
	
	public void sendAction(String action, String otherId, String msgText) {
		String content = String.format("%s,%s,%s,%s,%s%s%s\r\n", 
				action, Build.SERIAL, getNickName(), DateUtil.getNowTime(), 
				otherId, ClientThread.SPLIT_LINE, msgText);
		Log.d(TAG, "sendAction : " + content);
		Message msg = Message.obtain();
		msg.obj = content;
		if (mClientThread==null || mClientThread.mRecvHandler==null) {
			Log.d(TAG, "mClientThread or its mRecvHandler is null");
		} else {
			mClientThread.mRecvHandler.sendMessage(msg);
		}
	}
	
	public void setNickName(String nickName) {
		mApp.mNickName = nickName;
	}
	
	public String getNickName() {
		return mApp.mNickName;
	}
	
}
