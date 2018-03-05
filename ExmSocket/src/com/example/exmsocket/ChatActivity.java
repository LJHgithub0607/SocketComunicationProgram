package com.example.exmsocket;

import com.example.exmsocket.thread.ClientThread;
import com.example.exmsocket.util.DateUtil;
import com.example.exmsocket.util.MetricsUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity implements OnClickListener {
	private static final String TAG = "ChatActivity";

    private static Context mContext;
    private TextView tv_other;
    private EditText et_input;
    private static TextView tv_show;
    private static LinearLayout ll_show;
    private Button btn_send;
    private String mOtherId;
    private static int dip_margin;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = getApplicationContext();
        tv_other = (TextView) findViewById(R.id.tv_other);
        et_input = (EditText) findViewById(R.id.et_input);
        tv_show = (TextView) findViewById(R.id.tv_show);
        ll_show = (LinearLayout) findViewById(R.id.ll_show);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        
        dip_margin = MetricsUtil.dip2px(mContext, 5);
        Bundle bundle = getIntent().getExtras();
        mOtherId = bundle.getString("otherId", "");
        String desc = String.format("与%s聊天", bundle.getString("otherName", ""));
        tv_other.setText(desc);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_send) {
			String body = et_input.getText().toString();
			String append = String.format("%s %s\n%s",
					MainApplication.getInstance().getNickName(),
					DateUtil.formatTime(DateUtil.getNowTime()), body);
			appendMsg(Build.SERIAL, append);
            MainApplication.getInstance().sendAction(ClientThread.SENDMSG, mOtherId, body);
            et_input.setText("");
		}
	}
	
	private static void appendMsg(String deviceId, String append) {
		//tv_show.setText(tv_show.getText().toString() + append);
		int gravity = deviceId.equals(Build.SERIAL) ? Gravity.RIGHT : Gravity.LEFT;
		int bg_color = deviceId.equals(Build.SERIAL) ? 0xffccccff : 0xffffcccc;
		LinearLayout ll_append = new LinearLayout(mContext);
		LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		ll_params.setMargins(dip_margin, dip_margin, dip_margin, dip_margin);
		ll_append.setLayoutParams(ll_params);
		ll_append.setGravity(gravity);
		
		TextView tv_append = new TextView(mContext);
		tv_append.setText(tv_show.getText().toString() + append);
		tv_append.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tv_append.setLayoutParams(tv_params);
		tv_append.setGravity(gravity);
		tv_append.setBackgroundColor(bg_color);
		ll_append.addView(tv_append);

		ll_show.addView(ll_append);
	}

	public static class RecvMsgReceiver extends BroadcastReceiver {

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
					if (splitArray[0].equals(ClientThread.RECVMSG)) {
						String append = String.format("%s %s\n%s",
								splitArray[2], DateUtil.formatTime(splitArray[3]), body);
						appendMsg(splitArray[1], append);
					} else {
						String hint = String.format("%s\n%s", splitArray[0], body);
						Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

}
