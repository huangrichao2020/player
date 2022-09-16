## 依赖引入
教程 https://www.jianshu.com/p/f3b049a68ac6

- implementation 快速编译 对外不可见
- api == compileOnly
- compileOnly 仅编译 不参与打包 解决module与app导入同一库时的冲突问题
- buildscript.dependencies 里面是给gradle脚本自身用的插件。
- implementation('x'){  
     exclude module:'cglib' 不编译指定模块
     transitiv = false 不自动添加子依赖项
     force = true 强制依赖改版本
}

-  导入jar包
   - module下创建libs文件夹
   - implementation fileTree(dir: 'libs', include: ['*.jar'])

- 导入aar包
```
repositories {   

        flatDir {       

                dir "../${project.name}/libs"   

        }

}

dependencies {    

        implementation(name: 'aar名字', ext: 'aar') 

}

<!-- 全局声明aar包的路径 -->
allprojects {   
        repositories {       

                jcenter()       

                google()       

                flatDir {           

                        dirs "../moudle-A/libs,../moudle-B/libs,../moudle-C/libs".split(",")       

                }   

        }

}
```


- aar包中资源文件名与主工程内的资源名重复
  - 主工程的资源文件会覆盖aar包中的资源



# Hrctest
Android Demo App For Auto-Test With GTouch SDK

## DO WHAT
Help developers find their fault or weak point in codes,especially Technical solutions which are being rapidly iterateds.

## HOW TO DO
1. Android App --> GTouch SDK --> API Mock --> Attach the coordinates of View in WebView by JS based on Xpath. 
2. Emulating actions which human does above UI by Espresso 

## Intent
- 包括要执行操作的数据、应执行操作的组件类别以及其他相关说明


## DOM结构

- 根元素
- 子元素，，每个子元素有自己的属性
- 子子元素，子子元素的属性
- 很明显，一个标签元素可以抽象成 父标签，子元素，自身的属性等三个概念

## final
- 内部类调用的外部变量，必须用final修饰

## 容器

- window 应用的界面
- Activity = 容器
- View = 控件 
- View在android命名空间里的属性
```
android:id 控件ID
android:background 背景颜色
android:alpha 透明度 0~1 
android:visibility  boolean
android:clickable 是否响应单击事件

```

### TextView
- getText()
- setText(CharSequence text)
- setTextSize() float文本大小
- setTextColor


## Android 数据持久化
### 内部存储空间
/data/data/packageName/shared_prefs
/data/data/packageName/databases
/data/data/packageName/files
/data/data/packageName/cache

写入
```
String FILENAME = "hello_file";
String string = "hello world!";

FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
fos.write(string.getBytes());
fos.close();
```
### 外部存储空间
/storage/data

## DP,PX
不同的手机像素不同，即px不同。所以人们以320x480，像素密度160为基准单位，设1dp =¡¡¡ 1px。
此时480x800，像素密度240，此时1dp=1.5px。
通过统一的dp2px与px2dp方法可以使二者相互转换。


## Android崩溃信息分析
- Java，Native，ANR
- 进程/线程 UI线程
- 堆栈的栈顶与主线程当前的调用栈

    日志
/system/etc/event-log-tags


## Looper Handler与MessageQueue
`https://www.cnblogs.com/lixiansheng/p/11192281.html`
- Looper用于管理MessageQueue（先进先出），创建Looper自动创建MessageQueue，主线程自动创建Looper对象。
- 每个线程最多一个Looper与MessageQueue，Looper.dispatchMessage()会取出MessageQueue分发给Handler处理
- Handler会执行handleMessage()

## 自定义gradle插件
