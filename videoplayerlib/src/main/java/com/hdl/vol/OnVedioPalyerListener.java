package com.hdl.vol;

/**
 * Created by HDL on 2017/7/30.
 */

public interface OnVedioPalyerListener {
    void onStart();

    void onPrepare(long total);

    void onError(int errorCode, Throwable errorMsg);

    void onPuase(long currProgress);

    void onStop();
}
