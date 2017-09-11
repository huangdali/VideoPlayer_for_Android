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
     * 播放
     */
    void onStartPaly();

    /**
     * 播放出错
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, Throwable errorMsg);

    /**
     * 暂停
     * @param currProgress
     */
    void onPuase(long currProgress);

    /**
     * 停止
     */
    void onStop();

    /**
     * 播放完成
     */
    void onPlayFinished();
}
