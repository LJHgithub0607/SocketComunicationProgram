package com.example.exmsocket;

import com.example.exmsocket.thread.ClientThread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "MainActivity";

    private EditText et_name;
    private Button btn_ok;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_name = (EditText) findViewById(R.id.et_name);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_ok) {
			String nickName = et_name.getText().toString().trim();
			if (nickName.length() <= 0) {
				Toast.makeText(this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
			} else {
				MainApplication.getInstance().setNickName(nickName);
				MainApplication.getInstance().sendAction(ClientThread.LOGIN, "", "");
				Intent intent = new Intent(this, FriendListActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}
}
