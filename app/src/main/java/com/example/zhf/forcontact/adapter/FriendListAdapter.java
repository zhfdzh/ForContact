package com.example.zhf.forcontact.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhf.forcontact.R;
import com.example.zhf.forcontact.data.Friend;
import com.example.zhf.forcontact.util.GlobleVariable;

import java.util.List;

/**
 * Created by zhf on 2017/11/7.
 */

public class FriendListAdapter extends BaseAdapter {

    private List<Friend> mList ;
    private Context mContext;

    public FriendListAdapter(Context context, List<Friend> list){
        Log.d(GlobleVariable.TAG + "frendAdapter","enter FriendListAdapter() : list.size() = " + list.size());

        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(GlobleVariable.TAG + "frendAdapter","enter FriendListAdapter getView()");

        View view;
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.friend_list_item,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        view = convertView;
        holder = (ViewHolder) view.getTag();
        holder.headIcon.setBackgroundResource(mList.get(position).getHeadIcon());
        holder.nikeName.setText(mList.get(position).getNickName());
        holder.lastChatTime.setText(mList.get(position).getLastestChatTime());
        return view;
    }

    private class ViewHolder{
        public ViewHolder(View view){
            this.headIcon = view.findViewById(R.id.firend_head_icon);
            this.nikeName = view.findViewById(R.id.friend_nickname);
            this.lastChatTime = view.findViewById(R.id.last_time);
        }
        ImageView headIcon;
        TextView nikeName;
        TextView lastChatTime;
    }
}