package com.hdl.videopalyerdemo;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    private String url = "http://oss.cloudlinks.cn/011862167_2880011_1001/1515394990268.m3u8?Expires=1515482321&OSSAccessKeyId=LTAIAxqhixFoJsvp&Signature=6%2BfBDPp%2B%2BCVGRrOpa17mFPApeis%3D&x-oss-process=hls%2Fsign";
    private String url1 = "http://oss.cloudlinks.cn/011862167_2880011_1001/1515394990268.m3u8?Expires=1515482321&OSSAccessKeyId=LTAIAxqhixFoJsvp&Signature=6%2BfBDPp%2B%2BCVGRrOpa17mFPApeis%3D&x-oss-process=hls%2Fsign";
    private ProgressDialog mProgressDialog;
    private SeekBar sbProgress;
    private ImageView ivScreen;
    private boolean isSeekToed = true;
    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivScreen = (ImageView) findViewById(R.id.iv_screen);
        sbProgress = (SeekBar) findViewById(R.id.sb_progress);
        etUrl = (EditText) findViewById(R.id.et_url);
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
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
        vpPlayer.getTextureView().setBufferTimeMax(13.0f);
        vpPlayer.getTextureView().setTimeout(30, 60);
        vpPlayer.play(url, new OnVedioPalyerListener() {
            @Override
            public void onStart() {
                mProgressDialog.show();
                Log.e("hdltag", "onStart(MainActivity.java:69):onStart");
            }

            @Override
            public void onPrepare(long total) {
                sbProgress.setMax((int) total);
                Log.e("hdltag", "onPrepare(MainActivity.java:75):onPrepare");
                mProgressDialog.dismiss();
            }

            /**
             * 开始播放
             */
            @Override
            public void onStartPaly() {
                Log.e("hdltag", "onStartPaly(MainActivity.java:84):onStartPaly");
//                vpPlayer.pausePlay();
            }

            @Override
            public void onError(int errorCode, Throwable errorMsg) {
                Log.e("hdltag", "onError(MainActivity.java:89):" + errorCode + "\t" + errorMsg);
                mProgressDialog.dismiss();
                Toast.makeText(MainActivity.this, "视频url失效", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPuase(long currProgress) {
                Log.e("hdltag", "onPuase(MainActivity.java:96):暂停l");
            }

            @Override
            public void onStop() {
                Log.e("hdltag", "onStop(MainActivity.java:101):onStop");
            }

            /**
             * 播放完成
             */
            @Override
            public void onPlayFinished() {
                Log.e("hdltag", "onPlayFinished(MainActivity.java:104):播放完成了。。。。。。。。");
            }

            /**
             * 播放中
             *
             * @param curBuffPercent
             */
            @Override
            public void onPlaying(int curBuffPercent) {
//                Log.e("hdltag", "onPlaying(MainActivity.java:119):" + curBuffPercent);
            }

            @Override
            public void onReload() {
                Log.e("hdltag", "onReload(MainActivity.java:124):reload");
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

//        if (vpPlayer != null) {
//            vpPlayer.runInBackground(true);
//        }
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

    public void onChanger(View view) {
        String url = etUrl.getText().toString();
        vpPlayer.reload(url);
    }

    public void onPlay(View view) {
        vpPlayer.startPlay();
    }

    public void onPause(View view) {
        vpPlayer.pausePlay();
    }

    public void onStopPlay(View view) {
        vpPlayer.stopPlay();
    }

    public void onReload(View view) {
        vpPlayer.reload(url1);
    }
}

