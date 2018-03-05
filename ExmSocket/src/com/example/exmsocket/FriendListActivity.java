package com.example.exmsocket;

import java.util.ArrayList;

import com.example.exmsocket.adapter.Friend;
import com.example.exmsocket.adapter.FriendListAdapter;
import com.example.exmsocket.thread.ClientThread;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class FriendListActivity extends Activity implements OnClickListener {
	private static final String TAG = "FriendListActivity";

    private static Context mContext;
    private static ListView lv_friend;
    private Button btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mContext = getApplicationContext();
        lv_friend = (ListView) findViewById(R.id.lv_friend);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(this);
    }
    
    @Override
    protected void onResume() {
		mHandler.postDelayed(mRefresh, 500);
    	super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	MainApplication.getInstance().sendAction(ClientThread.LOGOUT, "", "");
    	super.onDestroy();
    }
    
    private Handler mHandler = new Handler();
    private Runnable mRefresh = new Runnable() {
		@Override
		public void run() {
	        MainApplication.getInstance().sendAction(ClientThread.GETLIST, "", "");
		}
    };

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_refresh) {
			mHandler.post(mRefresh);
		}
	}
    
	public static class GetListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				Log.d(TAG, "onReceive");
				String content = intent.getStringExtra(ClientThread.CONTENT);
				if (mContext != null && content != null && content.length() > 0) {
					int pos = content.indexOf(ClientThread.SPLIT_LINE);
					String head = content.substring(0, pos);
					String body = content.substring(pos + 1);
					String[] splitArray = head.split(ClientThread.SPLIT_ITEM);
					if (splitArray[0].equals(ClientThread.GETLIST)) {
						String[] bodyArray = body.split("\\|");
						ArrayList<Friend> friendList = new ArrayList<Friend>();
						for (int i = 0; i < bodyArray.length; i++) {
							String[] itemArray = bodyArray[i].split(ClientThread.SPLIT_ITEM);
							if (bodyArray[i].length() > 0 && itemArray != null && itemArray.length >= 3) {
								friendList.add(new Friend(itemArray[0], itemArray[1], itemArray[2]));
							}
						}
						if (friendList.size() > 0) {
							FriendListAdapter adapter = new FriendListAdapter(mContext, friendList);
							lv_friend.setAdapter(adapter);
							lv_friend.setOnItemClickListener(adapter);
						}
					} else {
						String hint = String.format("%s\n%s", splitArray[0], body);
						Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

}
