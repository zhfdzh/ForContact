package com.example.zhf.forcontact;

import android.app.Activity;
import android.content.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.example.zhf.forcontact.util.*;


public class LoginActivity extends Activity implements View.OnClickListener,TextWatcher{

    private android.app.ActionBar mActionBar;
    private EditText    mUserName;
    private EditText    mPassword;
    private Button      mlogin;
    private ImageView   mQQLogin;
    private ImageView   mWechatLogin;
    private ImageView   mWeiboLogin;
    private SharedPreferences mSharepreferenced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(false);              //设置显示actionbar的返回按钮
//            mActionBar.setDisplayShowTitleEnabled(false);            //设置显示actionbar的图标
            mActionBar.setTitle(getResources().getString(R.string.back));
        }
        ActionBarUtil.setStatusBarUpper(this);
        setContentView(R.layout.activity_login);
        initView();

        setLoginListener();
    }
    private void initView() {
        mUserName = findViewById(R.id.userName);
        mPassword = findViewById(R.id.password);
        mlogin = findViewById(R.id.app_login);
        mQQLogin = findViewById(R.id.qq_login);
        mWechatLogin = findViewById(R.id.wecaht_login);
        mWeiboLogin = findViewById(R.id.weibo_login);
        Log.d("LoginActivity","" + (mQQLogin == null));

        mSharepreferenced = getSharedPreferences(GlobleVariable.USER_PASSWORD_SHAREAPREFERENCES,MODE_PRIVATE);
        String username = mSharepreferenced.getString(GlobleVariable.USERNAME,"");
        String password = mSharepreferenced.getString(GlobleVariable.PASSWORD,"");
        mUserName.setText(username);
        mPassword.setText(password);
    }

    private void setLoginListener() {
        mlogin.setOnClickListener(this);
        mQQLogin.setOnClickListener(this);
        mWechatLogin.setOnClickListener(this);
        mWeiboLogin.setOnClickListener(this);
        mUserName.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.d("LoginActivity","enter onClick");

        switch(v.getId()){
            case R.id.app_login:
                String user = mUserName.getText().toString();
                String pass = mPassword.getText().toString();

                if(user.equals("") || pass.equals("")){
                    Toast.makeText(this,"用户名密码不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(pass.length() < GlobleVariable.PASSWORD_MINLENGTH){
                        Toast.makeText(this,"密码长度不能低于6位",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("LoginActivity","user: "+ user + " --- pass: " + pass);
                    UserLoginTask loginTask = new UserLoginTask(user,pass);
                    loginTask.execute();
                    mlogin.setEnabled(false);
                }
                break;
            case R.id.wecaht_login:

                break;
            case R.id.weibo_login:

                break;
            case R.id.qq_login:

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("LoginActivity","enter onEditorAction");

        String user = mUserName.getText().toString();
        String pass = mPassword.getText().toString();
        if( !(user.equals("")) && !(pass.equals(""))){
            mlogin.setEnabled(true);
            mlogin.setBackground(getDrawable(R.drawable.login_button_select_shape));

        }else {
            mlogin.setEnabled(false);
            mlogin.setBackground(getDrawable(R.drawable.login_button_style_gray));
        }
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean>{

        String userNameString;
        String passWordString;

        UserLoginTask(String userName, String passWord){
            this.userNameString = userName;
            this.passWordString = passWord;
        }

        @Override
        protected void onPreExecute() {      // 异步任务执行前执行，做一些初始化任务
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void[] params) {
            Log.d("LoginActivity","enter doInBackground");
            try {
                // Simulate network access.
                Thread.sleep(2000);
                if(userNameString.equals("zhuhf") && passWordString.equals("123456")){
                    return true;
                }
            } catch (InterruptedException e) {
                return false;
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Void[] values) {       //在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上。
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean o) {       // 当后台操作结束时，此方法将会被调用
            Log.d("LoginActivity","enter onPostExecute");
            super.onPostExecute(o);
            if(o){
                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = mSharepreferenced.edit();
                editor.putString(GlobleVariable.USERNAME,userNameString);
                editor.putString(GlobleVariable.PASSWORD,passWordString);
                editor.apply();
            }else {
                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_LONG).show();
            }
            mlogin.setEnabled(true);
        }

        @Override
        protected void onCancelled() {     // 方法用于在取消执行中的任务时更改UI
            super.onCancelled();
        }
    }
}
