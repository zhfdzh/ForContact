package com.example.zhf.forcontact.data;

/**
 * Created by zhf on 2017/11/7.
 */

public class Temp {

    // 拿到code后 通过 http的GET请求方式: https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
    // 获取access_token 返回的数据结构如下：

    /*{
        "access_token":"ACCESS_TOKEN",
            "expires_in":7200,
            "refresh_token":"REFRESH_TOKEN",
            "openid":"OPENID",
            "scope":"SCOPE"
    }*/

// 通过access_token 获取个人信息：http的GET请求方式: https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID

// 返回的数据结构如下：

   /* {
        "openid":"OPENID",
            "nickname":"NICKNAME",
            "sex":1,
            "province":"PROVINCE",
            "city":"CITY",
            "country":"COUNTRY",
            "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
            "privilege":[
                "PRIVILEGE1",
                "PRIVILEGE2"
              ],
        "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"

    }*/
}
