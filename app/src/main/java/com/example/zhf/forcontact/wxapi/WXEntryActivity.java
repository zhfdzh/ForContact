package com.example.zhf.forcontact.wxapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.zhf.forcontact.util.GlobleVariable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.net.URLEncoder;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;
    private BaseResp resp = null;
    private String WX_APP_ID = "创建应用后得到的APP_ID";
    // 获取第一步的code后，请求以下链接获取access_token
    private String GetCodeRequest = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    // 获取用户个人信息
    private String GetUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
    private String WX_APP_SECRET = "创建应用后得到的APP_SECRET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        finish();
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        Log.d(GlobleVariable.TAG +"WXEntry","enter WXEntryActivity onResp: resp.errcode = " + resp.errCode );
        String result = "";
        int type = resp.getType(); //类型：分享还是登录
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//                String code = ((SendAuth.Resp) resp).code;
                String code = ((SendAuth.Resp) resp).token;
                String state = ((SendAuth.Resp) resp).state;

            /*
             * 将你前面得到的AppID、AppSecret、code，拼接成URL 获取access_token等等的信息(微信)
             */
                String get_access_token = getCodeRequest(code);
                Log.d(GlobleVariable.TAG +"WXEntry"," WXEntryActivity: get_access_token = " + get_access_token);
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(get_access_token, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                        Log.d(GlobleVariable.TAG +"WXEntry"," WXEntryActivity : onSuccess " );

                        super.onSuccess(statusCode, headers, response);
                        // TODO Auto-generated method stub
                        try {
                            if (!response.equals("")) {
                                String access_token = response.getString("access_token");
                                String openid = response.getString("openid");
                                String get_user_info_url = getUserInfo(access_token, openid);
                                Log.d(GlobleVariable.TAG +"WXEntry"," onSuccess: get_user_info_url =  " + get_user_info_url );

                                getUserInfo(get_user_info_url);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();}
                    }
                });

                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "取消登录";
                /*if (type == RETURN_MSG_TYPE_LOGIN) {
                    result = "取消了微信登录";
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    result = "取消了微信分享";
                }*/
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
                result = "发送返回";
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }

    /**
     * 通过拼接的用户信息url获取用户信息
     *
     * @param user_info_url
     */
    private void getUserInfo(String user_info_url) {
        Log.d(GlobleVariable.TAG +"WXEntry"," getUserInfo " );

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(user_info_url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(GlobleVariable.TAG +"WXEntry"," getUserInfo :onSuccess " );

                try {
                    System.out.println("获取用户信息:" + response);
                    if (!response.equals("")) {
                        String openid = response.getString("openid");
                        String nickname = response.getString("nickname");
                        String headimgurl = response.getString("headimgurl");
                        Log.d(GlobleVariable.TAG +"WXEntry"," getUserInfo :onSuccess  { openid = "+ openid+"  nickname" + nickname + "  headimgurl = " +headimgurl + " }" );
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }

    /**
     * 获取access_token的URL（微信）
     *
     * @param code
     *            授权时，微信回调给的
     * @return URL
     */
    private String getCodeRequest(String code) {
        String result = null;
        GetCodeRequest = GetCodeRequest.replace("APPID",
                urlEnodeUTF8(WX_APP_ID));
        GetCodeRequest = GetCodeRequest.replace("SECRET",
                urlEnodeUTF8(WX_APP_SECRET));
        GetCodeRequest = GetCodeRequest.replace("CODE", urlEnodeUTF8(code));
        result = GetCodeRequest;
        return result;
    }

    /**
     * 获取用户个人信息的URL（微信）
     *
     * @param access_token
     *            获取access_token时给的
     * @param openid
     *            获取access_token时给的
     * @return URL
     */
    private String getUserInfo(String access_token, String openid) {
        String result = null;
        GetUserInfo = GetUserInfo.replace("ACCESS_TOKEN",
                urlEnodeUTF8(access_token));
        GetUserInfo = GetUserInfo.replace("OPENID", urlEnodeUTF8(openid));
        result = GetUserInfo;
        return result;
    }

    private String urlEnodeUTF8(String str) {
        String result = str;
        try {
            result = URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
