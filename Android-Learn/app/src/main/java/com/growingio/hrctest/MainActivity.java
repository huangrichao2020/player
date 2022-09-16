package com.growingio.hrctest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver;
import com.netease.nimlib.sdk.qchat.QChatService;
import com.netease.nimlib.sdk.qchat.QChatServiceObserver;
import com.netease.nimlib.sdk.qchat.event.QChatStatusChangeEvent;
import com.netease.nimlib.sdk.qchat.param.QChatLoginParam;
import com.netease.nimlib.sdk.qchat.result.QChatLoginResult;
import com.netease.nimlib.sdk.util.NIMUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{


        private EditText accountEt,passwordEt;//账号、密码的输入框
        private Button saveAndLoginBtn,showBtn;//保存、显示信息的按钮
        private SharedPreferences sharedPreferences;//共享参数
        private String username;//输入的用户名
        private String password;//输入的密码
        private String TAG = "圈组跑通中:";

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
                @Override
                public void onEvent(Boolean aBoolean) {
                    if (aBoolean != null && aBoolean) {
                        // 主进程初始化完毕，可以开始访问数据库
                        Log.d(TAG,"主进程初始化完毕");
                    }
                }
            }, true);
            NIMClient.init(this, loginInfo(), options());

            if (NIMUtil.isMainProcess(this)) {
                // 注意：以下操作必须在主进程中进行
                // 1、UI相关初始化操作
                // 2、相关Service调用
                initUI();
                //调用observeOnlineStatus方法监听 IM 登录状态。
                NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
                        new Observer<StatusCode> () {
                            public void onEvent(StatusCode status) {
                                //获取状态的描述
                                String desc = status.getDesc();
                                if (status.wontAutoLogin()) {
                                    // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                                    Log.d(TAG,"当前登录状态是"+desc);
                                }
                            }
                        }, true);
                //IM 同步状态
                NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(new Observer<LoginSyncStatus>() {
                    @Override
                    public void onEvent(LoginSyncStatus status) {
                        if (status == LoginSyncStatus.BEGIN_SYNC) {
                            Log.i(TAG, "login sync data begin");
                        } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
                            Log.i(TAG, "login sync data completed");
                        }
                    }
                }, true);
               // 监听圈组登录
                NIMClient.getService(QChatServiceObserver.class).observeStatusChange(new Observer<QChatStatusChangeEvent>() {
                    @Override
                    public void onEvent(QChatStatusChangeEvent qChatStatusChangeEvent) {
                        //当前状态
                        StatusCode status = qChatStatusChangeEvent.getStatus();
                    }
                },true);
            }

        }

    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        options.enabledQChatMessageCache = true;
        return options;
    }

    private LoginInfo loginInfo() {
        LoginInfo loginInfo = new LoginInfo(sharedPreferences.getString("username","huangrichao666"),sharedPreferences.getString("password","123456"));
        return loginInfo;
    }


    protected void initUI(){
            setContentView(R.layout.activity_main);

            //实例化操作
            accountEt=findViewById(R.id.accountEt);
            passwordEt= findViewById(R.id.passwordEt);
            saveAndLoginBtn=findViewById(R.id.saveAndLoginBtn);
            showBtn=findViewById(R.id.showBtn);

            //为保存、显示按钮设置监听事件
            saveAndLoginBtn.setOnClickListener(this);
            showBtn.setOnClickListener(this);
        }

    public void doIMLogin() {
        LoginInfo info = loginInfo();
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        Log.i(TAG, "login success");
                        // your code
                    }

                    @Override
                    public void onFailed(int code) {
                        if (code == 302) {
                            Log.i(TAG, "账号密码错误");
                            // your code
                        } else {
                            Log.i(TAG, "登录失败，原因为"+code);

                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        Log.i(TAG, exception.getStackTrace().toString());
                    }
                };

        //执行手动登录
        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
    }

    public void doQChatLogin(){
        QChatLoginParam loginParam = new QChatLoginParam();
        NIMClient.getService(QChatService.class).login(loginParam).setCallback(new RequestCallback<QChatLoginResult>() {
            @Override
            public void onSuccess(QChatLoginResult result) {
                Log.i(TAG, "login success");
                // your code
            }

            @Override
            public void onFailed(int code) {
                Log.i(TAG, "login failed and code is "+code);
                // your code
            }

            @Override
            public void onException(Throwable exception) {
                Log.i(TAG, exception.getStackTrace().toString());

            }
        });
    }

        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.saveAndLoginBtn:
                    username=accountEt.getText().toString().trim();//获取用户名输入框中的内容，用trim()去掉首尾空格
                    password=passwordEt.getText().toString().trim();
                    if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password))
                    {
                        Toast.makeText(MainActivity.this,"用户名或密码不能为空！",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("username",username);
                        editor.putString("password",password);
                        editor.commit();
                        Toast.makeText(MainActivity.this,"保存成功！",Toast.LENGTH_SHORT).show();
                    }



                    break;
                case R.id.showBtn:
                    sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
                    username=sharedPreferences.getString("username","huangrichao666");
                    password=sharedPreferences.getString("password","123456");
                    AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("显示保存的信息");
                    builder.setMessage("用户名："+username+"\n"+"密码："+password);
                    builder.setPositiveButton("知道了", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
                    builder.setCancelable(false);
                    builder.create().show();//调用create()方法时返回的是一个AlertDialog对象，再调用show()方法将对话框显示出来
                    break;
                default:

            }

        }

    }