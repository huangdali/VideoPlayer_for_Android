package com.hdl.vol;

/**
 * Created by HDL on 2017/7/30.
 */

public abstract class OnVedioPalyerListener {
    /**
     * 任务开始
     */
    public abstract void onStart();

    /**
     * 准备完成
     *
     * @param total
     */
    public abstract void onPrepare(long total);

    /**
     * 播放
     */
    public abstract void onStartPaly();

    /**
     * 播放出错
     *
     * @param errorCode
     * @param errorMsg
     */
    public abstract void onError(int errorCode, Throwable errorMsg);

    /**
     * 暂停
     *
     * @param currProgress
     */
    public abstract void onPuase(long currProgress);

    /**
     * 停止
     */
    public abstract void onStop();

    /**
     * 播放完成
     */
    public abstract void onPlayFinished();

    /**
     * 播放中
     */
    public void onPlaying(int curBuffPercent) {
    }

    /**
     * 重新加载的时候回调
     */
    public void onReload() {
    }
    /**
     * 重新加载成功的时候回调
     */
    public void onReloadSuccess() {
    }
}
