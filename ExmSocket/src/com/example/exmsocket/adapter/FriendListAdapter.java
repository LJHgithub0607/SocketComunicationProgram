package com.example.exmsocket.adapter;

import java.util.ArrayList;

import com.example.exmsocket.ChatActivity;
import com.example.exmsocket.R;
import com.example.exmsocket.util.DateUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FriendListAdapter extends BaseAdapter implements OnItemClickListener {

	private ArrayList<Friend> mFriendList;
	private LayoutInflater mInflater;
	private Context mContext;

	public FriendListAdapter(Context context, ArrayList<Friend> friendList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mFriendList = friendList;
	}

	@Override
	public int getCount() {
		return mFriendList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mFriendList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_friend, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Friend friend = mFriendList.get(position);
		holder.tv_name.setText(friend.nick_name);
		holder.tv_time.setText(DateUtil.formatTime(friend.login_time));
		return convertView;
	}

	public final class ViewHolder {
		public TextView tv_name;
		public TextView tv_time;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Friend friend = mFriendList.get(position);
		Intent intent = new Intent(mContext, ChatActivity.class);
		intent.putExtra("otherId", friend.device_id);
		intent.putExtra("otherName", friend.nick_name);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

}
