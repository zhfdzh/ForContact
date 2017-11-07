package com.example.zhf.forcontact.data;

import android.location.Location;
import android.widget.ImageView;

/**
 * Created by zhf on 2017/11/7.
 */

public class Friend {
    public String mNickName;
    public String mAccount;
    public String mLastestChat;
    public String mLastestChatTime;
    public int mHeadIcon;
    public Location mLocation;       //保存位置信息

    public Friend(){}
    public Friend(String name, String time, int headIcon){
        this.mNickName = name;
        this.mLastestChatTime = time;
        this.mHeadIcon = headIcon;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location mLocation) {
        this.mLocation = mLocation;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String mNickName) {
        this.mNickName = mNickName;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String mAccount) {
        this.mAccount = mAccount;
    }

    public String getLastestChat() {
        return mLastestChat;
    }

    public void setLastestChat(String mLastestChat) {
        this.mLastestChat = mLastestChat;
    }

    public String getLastestChatTime() {
        return mLastestChatTime;
    }

    public void setLastestChatTime(String mLastestChatTime) {
        this.mLastestChatTime = mLastestChatTime;
    }

    public int getHeadIcon() {
        return mHeadIcon;
    }

    public void setHeadIcon(int mHeadIcon) {
        this.mHeadIcon = mHeadIcon;
    }
}
