# ADB指令
多机环境精准下达指令

- adb devices 获取设备号SN
- adb -s cf264b8f shell 

指定adb server端口 默认5037

- adb -P 5037 start-server

无线连接
- 手机连线电脑
- adb tcpip 5555

- adb connect 10.20.20.152(手机同一局域网下的ip)

- 连接失败则adb kill-server 重启服务

- 终端显示connect 10.20.20.152后可以把线拔掉。

第三方应用列表

- adb shell pm list packages -3

包名包含某字符串的应用

- adb shell pm list packages mazhuang

adb install 实际是分三步完成

- push apk 文件到 /data/local/tmp。
- 调用 pm install 安装。

- 删除 /data/local/tmp 下的对应 apk 文件。

卸载应用
- adb uninstall [-k] <packagename>
- 保留数据和缓存目录


查看前台界面的app
- adb shell dumpsys activity activities | grep mFocusedActivity

查看与某app相关的Services
- adb shell dumpsys activity services [<packagename>]

查看应用详情
- userId，代码路径，版本信息，权限信心，授予状态，签名版本
- adb shell dumpsys package com.growingio.gtouch

启动应用、调起Activity
- 调起微信主界面。并传给它 string 数据键值对 toast - hello, world
- adb shell am start -n org.mazhuang.boottimemeasure/.MainActivity --es "toast" "hello, world"
- -n component 
- -a action 比如某个VIEW
- c category 比如某个CONTACTS
表示调起微信的某 Service。
- adb shell am startservice -n com.android.systemui/.SystemUIService

广播模拟app测试场景
    向指定组件发送广播，触发该组件的某些功能
只向 org.mazhuang.boottimemeasure/.BootCompletedReceiver 广播 BOOT_COMPLETED

- adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -n org.mazhuang.boottimemeasure/.BootCompletedReceiver


action|	触发时机
---|---
android.net.conn.CONNECTIVITY_CHANGE	        | 网络连接发生变化
android.intent.action.SCREEN_ON	                | 屏幕点亮
android.intent.action.SCREEN_OFF	            | 屏幕熄灭
android.intent.action.BATTERY_LOW	            | 电量低，会弹出电量低提示框
android.intent.action.BATTERY_OKAY	            | 电量恢复了
android.intent.action.BOOT_COMPLETED	        | 设备启动完毕
android.intent.action.DEVICE_STORAGE_LOW        |存储空间过低
android.intent.action.DEVICE_STORAGE_OK	        | 存储空间恢复
android.intent.action.PACKAGE_ADDED	            | 安装了新的应用
android.net.wifi.STATE_CHANGE	WiFi            | 连接状态发生变化
android.net.wifi.WIFI_STATE_CHANGED	WiFi| 状态变为启用/关闭/正在启动/正在关闭/未知
android.intent.action.BATTERY_CHANGED	        |     电池电量发生变化
android.intent.action.INPUT_METHOD_CHANGED	    |  系统输入法发生变化
android.intent.action.ACTION_POWER_CONNECTED    | 外部电源连接
android.intent.action.ACTION_POWER_DISCONNECTED |  外部电源断开连接
android.intent.action.DREAMING_STARTED	        | 系统开始休眠
android.intent.action.DREAMING_STOPPED	        | 系统停止休眠
android.intent.action.WALLPAPER_CHANGED	        | 壁纸发生变化
android.intent.action.HEADSET_PLUG	            | 插入耳机
android.intent.action.MEDIA_UNMOUNTED           |卸载外部介质
android.intent.action.MEDIA_MOUNTED	            |挂载外部介质
android.os.action.POWER_SAVE_MODE_CHANGED	    |省电模式开启

强行停止应用
- adb shell am force-stop com.qihoo360.mobilesafe

强行分配低内存
- ps | grep growingio
- adb shell am send-trim-memory 12345 RUNNING_LOW

推送文件到手机上
- adb push ~/sr.mp4 /sdcard/
- 

模拟按键输入
- adb shell input keyevent <keycode>

keycode|含义
---|---
3	| HOME 键
4	| 返回键
5	| 打开拨号应用
6	| 挂断电话
24	| 增加音量
25	| 降低音量
26	| 电源键
27	| 拍照（需要在相机应用里）
64	| 打开浏览器
82	| 菜单键
85	| 播放/暂停
86	| 停止播放
87	| 播放下一首
88	| 播放上一首
122	| 移动光标到行首或列表顶部
123	| 移动光标到行末或列表底部
126	| 恢复播放
127	| 暂停播放
164	| 静音
176	| 打开系统设置
187	| 切换应用
207	| 打开联系人
208	| 打开日历
209	| 打开音乐
210	| 打开计算器
220	| 降低屏幕亮度
221	| 提高屏幕亮度
223	| 系统休眠
224	| 点亮屏幕
231	| 打开语音助手
276	| 如果没有 wakelock 则让系统休眠

触发应用
- adb shell am start -n com.growingio.gtouch/.MainActivity

通过adb输入操作
- 划屏操作 adb shell input swipe 300 1000 300 10 
- 输入文本 adb shell input text hello

查看日志
- 底层的 Linux 内核日志输出到 /proc/kmsg，Android 的日志输出到 /dev/log
V —— Verbose（最低，输出得最多）
D —— Debug
I —— Info
W —— Warning
E —— Error
F —— Fatal
S —— Silent（最高，啥也不输出）

表示输出 tag ActivityManager 的 Info 以上级别日志，输出 tag MyApp 的 Debug 以上级别日志，及其它 tag 的 Silent 级别日志（即屏蔽其它 tag 日志）。
- adb logcat ActivityManager:I MyApp:D *:S

推荐查看日志方法
- adb logcat -v long growingio:D

输出内核日志
- adb shell dmesg
- 

查看设备信息
- adb shell getprop ro.product.model

电池状况
- adb shell dumpsys battery
- level表示当前电量

屏幕分辨率
- adb shell wm size


获取Android_Id
- adb shell settings get secure android_id
- adb shell dumpsys iphonesubinfo

获取IMEI编号
- adb shell
- su
- service call iphonesubinfo 1

安卓版本
- adb shell getprop ro.build.version.release

IP地址
- adb shell ifconfig | grep Mask
- adb shell ifconfig wlan0
- adb shell netcfg

查看Mac地址
- adb shell
- su
- cat /sys/class/net/wlan0/address
- 
CPU信息
- adb shell cat /proc/cpuinfo

内存信息
- adb shell cat /proc/meminfo


系统属性
- adb shell cat /system/build.prop

属性名|含义
--|--
ro.build.version.sdk	|SDK 版本
ro.build.version.release|	Android 系统版本
ro.build.version.security_patch	Android| 安全补丁程序级别
ro.product.model|	型号
ro.product.brand	|品牌
ro.product.name	   | 设备名
ro.product.board	|处理器型号
ro.product.cpu.abilist|	CPU 支持的 abi 列表[节注一]
persist.sys.isUsbOtgEnabled|	是否支持 OTG
dalvik.vm.heapsize	|每个应用程序的内存上限
ro.sf.lcd_density	|屏幕密度


-单独查看： adb shell getprop 属性名 

修改设置
cat /data/data/com.android.providers.settings/databases/settings.db

修改分辨率
adb shell wm size 480*1024
adb shell wm size reset


### 屏幕截图
- adb exec-out screencap -p > sc
- adb shell screencap -p /sdcard/sc.png
- adb pull /sdcard/sc.png /Users/huangrichao/Desktop
## 安卓分层
```
APP-->API Collection-->Function Library-->Linux Core
```

## 屏幕录制
- adb shell screenrecord /sdcard/filename.mp4
- adb pull /sdcard/filename.mp4


## Monkey
- adb shell monkey -p com.growingio.gtouch -v 20

重启到Fastboot模式
- adb reboot bootloader
- adb reboot recovery

更新系统
- adb sideload <path-to-update.zip>

## 查看进程
- adb shell ps

USER|所属用户
-|-
PID	|进程ID
PPID	|父进程 ID
NAME|	程名
## 实时资源占用情况
- adb shell top -d 5 5秒刷新一次

列名|	含义
-|-
PID	|进程 ID
PR	|优先级
CPU%|	当前瞬间占用 CPU 百分比
S	|进程状态（R=运行，S=睡眠，T=跟踪/停止，Z=僵尸进程）
#THR|	线程数
VSS	Virtual Set Size| 虚拟耗用内存（包含共享库占用的内存）
RSS	Resident Set Size| 实际使用物理内存（包含共享库占用的内存）
PCY	|调度策略优先级，SP_BACKGROUND/SPFOREGROUND
UID	|进程所有者的用户 ID
NAME|	进程名


## 常用指令
命令|	功能
-|-
cat|	显示文件内容
cd	|切换目录
chmod|	改变文件的存取模式/访问权限
df	|查看磁盘空间使用情况
grep|	过滤输出
kill|	杀死指定 PID 的进程
ls	|列举目录内容
mount|	挂载目录的查看和管理
mv	|移动或重命名文件
ps|	查看正在运行的进程
rm|	删除文件
top|	查看进程的资源占用情况
## Monitor应用运行监视器
- Logcat
## ADB指令
- adb push 推送文件到设备
- adb pull 拉取文件到本地



