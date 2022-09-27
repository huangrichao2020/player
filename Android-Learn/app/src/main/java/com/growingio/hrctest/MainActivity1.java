//package com.growingio.hrctest;
//
//import static com.growingio.hrctest.SharePreferenceUtils.SP_CHANNELID;
//import static com.growingio.hrctest.SharePreferenceUtils.SP_PASSWORD;
//import static com.growingio.hrctest.SharePreferenceUtils.SP_READY;
//import static com.growingio.hrctest.SharePreferenceUtils.SP_SERVERID;
//import static com.growingio.hrctest.SharePreferenceUtils.SP_USERNAME;
//import static com.growingio.hrctest.SharePreferenceUtils.getLong;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//import android.widget.Toast;
//
//import com.netease.nimlib.sdk.NIMClient;
//import com.netease.nimlib.sdk.Observer;
//import com.netease.nimlib.sdk.RequestCallback;
//import com.netease.nimlib.sdk.SDKOptions;
//import com.netease.nimlib.sdk.StatusCode;
//import com.netease.nimlib.sdk.auth.AuthService;
//import com.netease.nimlib.sdk.auth.AuthServiceObserver;
//import com.netease.nimlib.sdk.auth.LoginInfo;
//import com.netease.nimlib.sdk.auth.constant.LoginSyncStatus;
//import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver;
//import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
//import com.netease.nimlib.sdk.qchat.QChatChannelService;
//import com.netease.nimlib.sdk.qchat.QChatMessageService;
//import com.netease.nimlib.sdk.qchat.QChatServerService;
//import com.netease.nimlib.sdk.qchat.QChatService;
//import com.netease.nimlib.sdk.qchat.QChatServiceObserver;
//import com.netease.nimlib.sdk.qchat.enums.QChatApplyJoinMode;
//import com.netease.nimlib.sdk.qchat.enums.QChatChannelMode;
//import com.netease.nimlib.sdk.qchat.enums.QChatChannelType;
//import com.netease.nimlib.sdk.qchat.enums.QChatInviteMode;
//import com.netease.nimlib.sdk.qchat.event.QChatStatusChangeEvent;
//import com.netease.nimlib.sdk.qchat.model.QChatChannel;
//import com.netease.nimlib.sdk.qchat.model.QChatMessage;
//import com.netease.nimlib.sdk.qchat.model.QChatServer;
//import com.netease.nimlib.sdk.qchat.model.QChatServerMember;
//import com.netease.nimlib.sdk.qchat.param.QChatCreateChannelParam;
//import com.netease.nimlib.sdk.qchat.param.QChatCreateServerParam;
//import com.netease.nimlib.sdk.qchat.param.QChatGetChannelMembersByPageParam;
//import com.netease.nimlib.sdk.qchat.param.QChatInviteServerMembersParam;
//import com.netease.nimlib.sdk.qchat.param.QChatLoginParam;
//import com.netease.nimlib.sdk.qchat.param.QChatSendMessageParam;
//import com.netease.nimlib.sdk.qchat.result.QChatCreateChannelResult;
//import com.netease.nimlib.sdk.qchat.result.QChatCreateServerResult;
//import com.netease.nimlib.sdk.qchat.result.QChatGetChannelMembersByPageResult;
//import com.netease.nimlib.sdk.qchat.result.QChatInviteServerMembersResult;
//import com.netease.nimlib.sdk.qchat.result.QChatLoginResult;
//import com.netease.nimlib.sdk.qchat.result.QChatSendMessageResult;
//import com.netease.nimlib.sdk.util.NIMUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener{
//
//
//    private EditText accountEt,passwordEt,inviteExt,messageExt;//账号、密码的输入框
//    private String username,password, TAG = "圈组跑通中:";
//    private Long serverId , channelId = 0L;
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        //全局判断是否创建了圈组服务器和频道并加入
//        SharePreferenceUtils.putBoolean(this,SP_READY,false);
//        NIMClient.init(this, loginInfo(), options());
//
//        NIMClient.getService(SdkLifecycleObserver.class).observeMainProcessInitCompleteResult(new Observer<Boolean>() {
//            @Override
//            public void onEvent(Boolean aBoolean) {
//                if (aBoolean != null && aBoolean) {
//                    // 主进程初始化完毕，可以开始访问数据库
//                    Log.d(TAG,"主进程初始化完毕");
//                }
//            }
//        }, true);
//
//        if (NIMUtil.isMainProcess(this)) {
//            // 注意：以下操作必须在主进程中进行
//            // 1、UI相关初始化操作
//            // 2、相关Service调用
//            initUI();
//            //调用observeOnlineStatus方法监听 IM 登录状态。
//            NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(
//                    new Observer<StatusCode> () {
//                        public void onEvent(StatusCode status) {
//                            //获取状态的描述
//                            String desc = status.getDesc();
//                            if (status.wontAutoLogin()) {
//                                // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
//                                Log.d(TAG,"当前登录状态是"+desc);
//                            }
//                        }
//                    }, true);
//            //IM 同步状态
//            NIMClient.getService(AuthServiceObserver.class).observeLoginSyncDataStatus(new Observer<LoginSyncStatus>() {
//                @Override
//                public void onEvent(LoginSyncStatus status) {
//                    if (status == LoginSyncStatus.BEGIN_SYNC) {
//                        Log.i(TAG, "login sync data begin");
//                    } else if (status == LoginSyncStatus.SYNC_COMPLETED) {
//                        Log.i(TAG, "login sync data completed");
//                    }
//                }
//            }, true);
//            // 监听圈组登录
//            NIMClient.getService(QChatServiceObserver.class).observeStatusChange(new Observer<QChatStatusChangeEvent>() {
//                @Override
//                public void onEvent(QChatStatusChangeEvent qChatStatusChangeEvent) {
//                    //当前状态
//                    StatusCode status = qChatStatusChangeEvent.getStatus();
//                    if (status == StatusCode.LOGINED) {
//                        Log.i(TAG, "登录成功！ ");
//                    } else if (status == StatusCode.CONNECTING || status == StatusCode.LOGINING || status == StatusCode.SYNCING) {
//                        Log.i(TAG, "正在登录中！ ");
//                    } else {
//                        Log.i(TAG,"登录出现异常，报错码为：" + status.getValue());
//                    }
//                }
//            },true);
//        }
//        //接收方 调用QChatServiceObserver#observeReceiveMessage方法监听圈组消息接收。
//        NIMClient.getService(QChatServiceObserver.class).observeReceiveMessage(new Observer<List<QChatMessage>>() {
//            @Override
//            public void onEvent(List<QChatMessage> qChatMessages) {
//                //收到消息qChatMessages
//                for (QChatMessage qChatMessage : qChatMessages) {
//                    //处理消息
//                    Log.d(TAG,"收到圈组消息:"+qChatMessage.getContent());
//                }
//            }
//        }, true);
//
//        // 发送方调用QChatServiceObserver#observeMessageStatusChange)方法监听圈组消息发送状态。
//        NIMClient.getService(QChatServiceObserver.class).observeMessageStatusChange(new Observer<QChatMessage>() {
//            @Override
//            public void onEvent(QChatMessage qChatMessage) {
//                //收到状态变化的消息qChatMessage
//                Log.d(TAG,"发送圈组消息完成:"+ qChatMessage.getContent() + qChatMessage.getUuid());
//            }
//        }, true);
//    }
//
//    protected void initUI(){
//        setContentView(R.layout.activity_main);
//
//        //实例化操作
//        accountEt = findViewById(R.id.accountEt);
//        passwordEt = findViewById(R.id.passwordEt);
//        inviteExt = findViewById(R.id.inviteExt);
//        messageExt = findViewById(R.id.msgExt);
//        findViewById(R.id.saveAndLoginBtn).setOnClickListener(this);
//        findViewById(R.id.readyServer).setOnClickListener(this);
//        findViewById(R.id.readyChannel).setOnClickListener(this);
//        findViewById(R.id.showBtn).setOnClickListener(this);
//        findViewById(R.id.inviteExt).setOnClickListener(this);
//        findViewById(R.id.invitePeople).setOnClickListener(this);
//        findViewById(R.id.queryChannel).setOnClickListener(this);
//        findViewById(R.id.msgExt).setOnClickListener(this);
//        findViewById(R.id.sendMsg).setOnClickListener(this);
//        findViewById(R.id.call).setOnClickListener(this);
//        findViewById(R.id.endCall).setOnClickListener(this);
//        findViewById(R.id.createAndInviteRtcChannel).setOnClickListener(this);
//
//    }
//
//
//    @Override
//    public void onClick(View v)
//    {
//        switch (v.getId())
//        {
//            case R.id.saveAndLoginBtn:
//                username = accountEt.getText().toString().trim();//获取用户名输入框中的内容，用trim()去掉首尾空格
//                password = passwordEt.getText().toString().trim();
//                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password))
//                {
//                    showToast("用户名或密码不能为空！");
//                }
//                else
//                {
//                    SharePreferenceUtils.putString(this,SP_USERNAME,username);
//                    SharePreferenceUtils.putString(this,SP_PASSWORD,password);
//
//                }
//                doIMLogin();
//
//                break;
//
//            case R.id.readyServer:
//                if( SharePreferenceUtils.getBoolean(this,SP_READY,false) ){
//                    Log.d(TAG,"已创建过圈组服务器");
//                    showToast("已创建过圈组服务器");
//                } else{
//                    //创建服务器
//                    QChatCreateServerParam serverParam = new QChatCreateServerParam("跑通圈组文档之服务器");
//                    serverParam.setApplyJoinMode(QChatApplyJoinMode.AGREE_NEED_NOT);
//                    serverParam.setInviteMode(QChatInviteMode.AGREE_NEED_NOT);
//                    NIMClient.getService(QChatServerService.class).createServer(serverParam).setCallback(
//                            new RequestCallback<QChatCreateServerResult>() {
//                                @Override
//                                public void onSuccess(QChatCreateServerResult result) {
//                                    // 创建成功
//                                    SharePreferenceUtils.putBoolean(MainActivity.this,SP_READY,true);
//
//                                    QChatServer server = result.getServer();
//                                    SharePreferenceUtils.putLong(MainActivity.this,SP_SERVERID,server.getServerId());
//                                    Log.d(TAG,"创建圈组服务器成功"+server.getName() + " " + server.getServerId());
//                                }
//
//                                @Override
//                                public void onFailed(int code) {
//                                    // 创建失败，返回错误code
//                                    Log.d(TAG,"创建圈组服务器失败："+code);
//                                    showToast("创建圈组服务器失败："+code);
//                                }
//
//                                @Override
//                                public void onException(Throwable exception) {
//                                    // 创建异常
//                                    Log.d(TAG,exception.getStackTrace().toString());
//                                }
//                            });
//                }
//                break;
//
//            case R.id.readyChannel:
//                //创建频道
//                //建立一个消息类型的频道
//                QChatCreateChannelParam channelParam = new QChatCreateChannelParam(SharePreferenceUtils.getLong(this,SP_SERVERID), "跑通圈组文档之频道", QChatChannelType.MessageChannel);
//                channelParam.setCustom("自定义扩展");
//                channelParam.setTopic("主题");
//                //设置频道为公开频道
//                channelParam.setViewMode(QChatChannelMode.PUBLIC);
//                NIMClient.getService(QChatChannelService.class).createChannel(channelParam).setCallback(
//                        new RequestCallback<QChatCreateChannelResult>() {
//                            @Override
//                            public void onSuccess(QChatCreateChannelResult result) {
//                                //创建Channel成功,返回创建成功的Channel信息
//                                QChatChannel channel = result.getChannel();
//                                SharePreferenceUtils.putLong(MainActivity.this,SP_CHANNELID,channel.getChannelId());
//                                Log.d(TAG,"创建消息频道成功"+channel.getName() + " " + channel.getChannelId());
//                                showToast("创建消息频道成功");
//                            }
//
//                            @Override
//                            public void onFailed(int code) {
//                                //创建Channel失败，返回错误code
//                            }
//
//                            @Override
//                            public void onException(Throwable exception) {
//                                //创建Channel异常
//                            }
//                        });
//                break;
//            case R.id.showBtn:
//                username = SharePreferenceUtils.getString(this,SP_USERNAME);
//                password = SharePreferenceUtils.getString(this,SP_PASSWORD);
//                serverId = SharePreferenceUtils.getLong(this,SP_SERVERID);
//                channelId = SharePreferenceUtils.getLong(this,SP_CHANNELID);
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setTitle("显示保存的信息");
//                builder.setMessage("用户名：" + username
//                        + "\n" + "密码：" + password
//                        + "\n" + "服务器ID：" + serverId
//                        + "\n" + "频道ID： " + channelId);
//                builder.setPositiveButton("知道了", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        dialog.cancel();
//                    }
//                });
//                builder.setCancelable(false);
//                builder.create().show();//调用create()方法时返回的是一个AlertDialog对象，再调用show()方法将对话框显示出来
//                break;
//
//            case R.id.invitePeople:
//                String inviteAcconut = inviteExt.getText().toString().trim();
//                List<String> accids = new ArrayList<>();
//                accids.add(inviteAcconut);
//                QChatInviteServerMembersParam param = new QChatInviteServerMembersParam(getLong(this,SP_SERVERID),accids);
//                param.setPostscript("邀请你加入测试服务器");
//                NIMClient.getService(QChatServerService.class).inviteServerMembers(param).setCallback(
//                        new RequestCallback<QChatInviteServerMembersResult>() {
//                            @Override
//                            public void onSuccess(QChatInviteServerMembersResult result) {
//                                //打印邀请id和过期时间
//                                Log.d(TAG,"邀请id为 "+ result.getInviteServerMemberInfo().getRequestId() + " " + result.getInviteServerMemberInfo().getExpireTime() );
//                                showToast("邀请成功，邀请id为" + result.getInviteServerMemberInfo().getRequestId() );
//                                //邀请成功,会返回因为用户服务器数量超限导致失败的accid列表
//                                List<String> failedAccids = result.getFailedAccids();
//                            }
//
//                            @Override
//                            public void onFailed(int code) {
//                                //邀请失败，返回错误code
//                            }
//
//                            @Override
//                            public void onException(Throwable exception) {
//                                //邀请异常
//                            }
//                        });
//                break;
//
//            case R.id.queryChannel:
//                QChatGetChannelMembersByPageParam queryParam = new QChatGetChannelMembersByPageParam(getLong(this,SP_SERVERID),getLong(this,SP_CHANNELID),System.currentTimeMillis());
//                queryParam.setLimit(10);
//                NIMClient.getService(QChatChannelService.class).getChannelMembersByPage(queryParam).setCallback(new RequestCallback<QChatGetChannelMembersByPageResult>() {
//                    @Override
//                    public void onSuccess(QChatGetChannelMembersByPageResult result) {
//                        //查询成功,返回频道下成员列表
//                        List<QChatServerMember> members = result.getMembers();
//                        String membersList = "";
//                        for (QChatServerMember m : members) {
//                            membersList += m.getNick() + " : " + m.getAccid() + "\n";
//                        }
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        builder.setTitle("当前频道成员分别是");
//                        builder.setMessage("用户列表：" + membersList);
//                        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                dialog.cancel();
//                            }
//                        });
//                        builder.setCancelable(false);
//                        builder.create().show();//调用create()方法时返回的是一个AlertDialog对象，再调用show()方法将对话框显示出来
//
//                    }
//
//                    @Override
//                    public void onFailed(int code) {
//                        //查询失败，返回错误code
//                    }
//
//                    @Override
//                    public void onException(Throwable exception) {
//                        //查询异常
//                    }
//                });
//
//                break;
//
//            case R.id.sendMsg:
//                QChatSendMessageParam messageParam = new QChatSendMessageParam(getLong(this,SP_SERVERID),getLong(this,SP_CHANNELID), MsgTypeEnum.text);
//                messageParam.setBody(messageExt.getText().toString().trim());
//                messageParam.setMentionedAll(false);
//                messageParam.setHistoryEnable(true);
//
//                messageParam.setPushEnable(true);
//                messageParam.setNeedBadge(true);
//                messageParam.setNeedPushNick(true);
//                //通过QChatSendMessageParam构造一个QChatMessage
//                QChatMessage currentMessage = messageParam.toQChatMessage();
//
//                NIMClient.getService(QChatMessageService.class).sendMessage(messageParam).setCallback(new RequestCallback<QChatSendMessageResult>() {
//                    @Override
//                    public void onSuccess(QChatSendMessageResult result) {
//                        //发送消息成功,返回发送成功的消息具体信息
//                        QChatMessage message = result.getSentMessage();
//                        showToast("发消息成功:" + message.getContent());
//                    }
//
//                    @Override
//                    public void onFailed(int code) {
//                        //发送消息失败，返回错误code
//                    }
//
//                    @Override
//                    public void onException(Throwable exception) {
//                        //发送消息异常
//                    }
//                });
//
//            default:
//
//        }
//    }
//
//    private SDKOptions options() {
//        SDKOptions options = new SDKOptions();
//        options.appKey = "45c6af3c98409b18a84451215d0bdd6e";
//        options.enabledQChatMessageCache = true;
//        return options;
//    }
//
//    private LoginInfo loginInfo() {
//        SharePreferenceUtils.putString(this,SP_USERNAME,"huangrichao");
//        SharePreferenceUtils.putString(this,SP_PASSWORD,"123456");
//
//        LoginInfo loginInfo = new LoginInfo(SharePreferenceUtils.getString(this,SP_USERNAME),SharePreferenceUtils.getString(this,SP_PASSWORD));
//
//        return loginInfo;
//    }
//
//
//
//    public void doIMLogin() {
//        LoginInfo info = loginInfo();
//        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
//            @Override
//            public void onSuccess(LoginInfo param) {
//                Log.i(TAG, "IM login success! Begin to do qchat login!");
//                // your code
//                doQChatLogin();
//            }
//
//            @Override
//            public void onFailed(int code) {
//                if (code == 302) {
//                    Log.i(TAG, "账号密码错误");
//                    // your code
//                } else {
//                    Log.i(TAG, "登录失败，原因为"+code);
//
//                }
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                Log.i(TAG, exception.getStackTrace().toString());
//            }
//        };
//
//        //执行手动登录
//        NIMClient.getService(AuthService.class).login(info).setCallback(callback);
//    }
//
//    public void doQChatLogin(){
//        QChatLoginParam loginParam = new QChatLoginParam();
//        NIMClient.getService(QChatService.class).login(loginParam).setCallback(new RequestCallback<QChatLoginResult>() {
//            @Override
//            public void onSuccess(QChatLoginResult result) {
//                Log.i(TAG, "login success"+result.toString());
//                showToast( "圈组 login success");
//
//            }
//
//            @Override
//            public void onFailed(int code) {
//                Log.i(TAG, "login failed and code is "+code);
//                showToast("login failed and code is"+code);
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//                Log.i(TAG, exception.getStackTrace().toString());
//
//            }
//        });
//    }
//
//    private void showToast(String content){
//        Toast toast = Toast.makeText(this, content, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.show();
//    }
//
//}