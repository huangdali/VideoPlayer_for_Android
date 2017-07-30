package com.hdl.videopalyerdemo;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.hdl.vol.OnVedioPalyerListener;
import com.hdl.vol.VedioPlayer;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private VedioPlayer vpPlayer;
    private String url = "http://gwell-oss-test1.oss-cn-shenzhen.aliyuncs.com/video-123yun/2017-07-20%2017%3A00%3A00.m3u8?Expires=1501502790&OSSAccessKeyId=LTAIAxqhixFoJsvp&Signature=MfjA%2FuD7pWmoVXUgO3Uwn0YiOQU%3D";
    private ProgressDialog mProgressDialog;
    private SeekBar sbProgress;
    private ImageView ivScreen;
    private boolean isSeekToed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivScreen = (ImageView) findViewById(R.id.iv_screen);
        sbProgress = (SeekBar) findViewById(R.id.sb_progress);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e(TAG, "onProgressChanged:" + seekBar.getProgress());
                if (!isSeekToed) {
                    vpPlayer.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekToed = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekToed = true;
            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("加载中...");
        vpPlayer = (VedioPlayer) findViewById(R.id.vp_player);
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
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sbProgress.setProgress((int) vpPlayer.getCurrentPosition());
                    }
                });
            }
        }, 0, 1000);
    }

    private int curProgress = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vpPlayer.stopPlay();
        vpPlayer = null;
    }

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

    public void onScreen(View view) {
        ivScreen.setImageBitmap(vpPlayer.getScreenShot());
    }
}

