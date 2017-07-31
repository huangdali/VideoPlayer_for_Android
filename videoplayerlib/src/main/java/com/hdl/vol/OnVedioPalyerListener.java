package com.hdl.vol;

/**
 * Created by HDL on 2017/7/30.
 */

public interface OnVedioPalyerListener {
    /**
     * 任务开始
     */
    void onStart();

    /**
     * 准备完成
     * @param total
     */
    void onPrepare(long total);

    /**
     * 开始播放
     */
    void onStartPaly();

    /**
     * 播放出错了
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, Throwable errorMsg);

    /**
     * 暂停了
     * @param currProgress
     */
    void onPuase(long currProgress);

    /**
     * 停止了
     */
    void onStop();
}
