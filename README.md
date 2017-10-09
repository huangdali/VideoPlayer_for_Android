# VideoPlayer_for_Android

android端网络视频点播播放器

- 带截图功能
- 可后台播放
- 提供reload方法


>基于 [金山云点播播放器](https://github.com/ksvc/KSYMediaPlayer_Android) 修改而来，仅仅做学习使用，请勿用于生产环境

### 效果图

![](https://github.com/huangdali/VideoPlayer_for_Android/blob/master/screenshot.png)


### 引入：

```java
compile 'com.jwkj:VideoPlayer:v2.0.2'
```

### 添加混淆：
```java
-keep class com.hdl.vol.**{*;}
-keep class com.ksyun.media.player.**{ *; }
-keep class com.ksy.statlibrary.**{ *;}
```

### 播放：

```java
      vpPlayer.play(url, new OnVedioPalyerListener() {
            @Override
            public void onStart() {
                mProgressDialog.show();
                Log.e(TAG, "onStart: ");
            }

            @Override
            public void onPrepare(long total) {
                sbProgress.setMax((int) total);
                Log.e(TAG, "onPrepare: ");
                mProgressDialog.dismiss();
            }

            @Override
            public void onError(int errorCode, Throwable errorMsg) {
                Log.e(TAG, "onError: ");
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "视频url失效", Toast.LENGTH_SHORT).show();
            }
        });
```

### 前台与后台的切换：
```java
    @Override
    protected void onPause() {
        super.onPause();
        if (vpPlayer != null) {
           vpPlayer.runInBackground(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (vpPlayer != null) {
            vpPlayer.runInForeground();
        }
    }
```

### 获取截图：
```java
  public void onScreen(View view) {
        ivScreen.setImageBitmap(vpPlayer.getScreenShot());
    }
```

### reload视频源：
```java
 public void onChanger(View view) {
        String url = etUrl.getText().toString();
        vpPlayer.reload(url);
    }
```

### 释放资源：
```java
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vpPlayer.stopPlay();
        vpPlayer = null;
    }
```

## 版本记录

v2.0.2（ [2017.10.09]() ）

- 【修复】当正在播放视频，点击播放区域时，2秒之后播放按钮不会自动隐藏

v2.0.1（ [2017.10.09]() ）

- 【优化】播放状态优化

v1.1.9（ [2017.09.21]() ）

- 【修复】当播放时立刻暂停会导致播放状态错乱

v1.1.8（ [2017.09.20]() ）

- 【新增】加载完毕前出现加载动画

v1.1.7（ [2017.09.20]() ）

- 【新增】点击播放按钮回调方法onClickPlayIcon

v1.1.6（ [2017.09.20]() ）

- 【新增】reload回调方法

v1.1.5（ [2017.09.18]() ）

- 【修复】特殊条件下视频播放中会显示暂停按钮

v1.1.4（ [2017.09.12]() ）

- 【新增】播放中回调onPalying()

v1.1.3（ [2017.09.11]() ）

- 【优化】升级金山云sdk到最新版本

v1.1.2（ [2017.09.11]() ）

- 【新增】播放完成时回调

v1.0.8（ [2017.08.18]() ）

- 【修复】播放、暂停按钮

v1.0.7（ [2017.07.31]() ）

- 【新增】真正开始播放时回调

v1.0.6（ [2017.07.31]() ）

- 【新增】暂停时回调
- 【新增】停止播放时回调

v1.0.5（ [2017.07.31]() ）

- 【优化】jni库改为armeabi

v1.0.4（ [2017.07.30]() ）

- 【新增】获取TextureView对象
