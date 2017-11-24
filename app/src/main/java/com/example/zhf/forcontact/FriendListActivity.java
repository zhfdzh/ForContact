package com.example.zhf.forcontact;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.zhf.forcontact.adapter.FriendListAdapter;
import com.example.zhf.forcontact.data.Friend;
import com.example.zhf.forcontact.util.ActionBarUtil;
import com.example.zhf.forcontact.util.GlobleVariable;

import java.util.ArrayList;
import java.util.List;

public class FriendListActivity extends Activity {

    private List<Friend> mList = new ArrayList<>();
    private ListView mListView;
    private FriendListAdapter mAdapter;
    private android.app.ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(GlobleVariable.TAG + "frendslist","enter FriendListActivity oncreate()");
        super.onCreate(savedInstanceState);
        mActionBar = getActionBar();
        if(mActionBar != null){
            ActionBarUtil.setStatusBarUpper(this);
            mActionBar.setDisplayShowTitleEnabled(false);
            mActionBar.setDisplayHomeAsUpEnabled(false);
            mActionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setCustomView(R.layout.custom_action_bar);
        }
        setContentView(R.layout.activity_friend_list);
        getFriendList();
        mListView = (ListView) findViewById(R.id.friends_list);
        mAdapter = new FriendListAdapter(FriendListActivity.this,mList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend friend = mList.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putDouble(GlobleVariable.LATITUDE,friend.getLatitude());
                bundle.putDouble(GlobleVariable.LONGITUDE,friend.getLongitude());
                intent.putExtras(bundle);
                intent.setClass(FriendListActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Friend friend = mList.get(position);

        }
    };

    private void getFriendList(){
        Log.d(GlobleVariable.TAG + "frendslist","enter FriendListActivity getFriendList()");
        Friend friend = new Friend();
        for (int i=0; i<10; i++){
            mList.add(new Friend("朋友" +( i+ 1), i +2 +"分钟前",R.drawable.friends));
            mList.get(i).setLatitude(Math.random() * 90);
            mList.get(i).setLongitude(Math.random() * 180);
        }
    }
}
