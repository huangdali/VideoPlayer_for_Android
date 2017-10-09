package com.hdl.vol;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.ksyun.media.player.KSYTextureView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 视频播放器
 * Created by HDL on 2017/7/28.
 */

public class VedioPlayer extends LinearLayout {
    private static final String TAG = "VedioPlayer";
    private Context mContext;
    private KSYTextureView mTextureView;
    private boolean isLooping = false;//是否循环播放
    private boolean isPlaying = false;//是否正在播放
    private boolean isClickPlay = false;//是否点击过播放按钮了
    /**
     * 播放地址
     */
    private String url;
    //    private ImageView playSwitch;
    private RelativeLayout rlPlayer;
    private OnVedioPalyerListener onVedioPalyerListener;
    private MenuItemView lampItem;

    public VedioPlayer(Context context) {
        this(context, null);
    }

    public VedioPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VedioPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        View view = View.inflate(mContext, R.layout.videopayer, null);
        this.addView(view);
        initView(view);
        initData();
    }
    private void initView(View view) {
        lampItem = (MenuItemView) view.findViewById(R.id.iv_option_paly);
        lampItem.buildSwitchLoaddingType(R.mipmap.pause, R.mipmap.pause, R.mipmap.play, R.mipmap.play, R.mipmap.loading);
        lampItem.setFunType(MenuItemView.FunctionType.LAMP);
        lampItem.setLoadding(true);
        lampItem.setOnClickMenuItemViewListener(new MenuItemView.OnClickMenuItemViewListener() {
            @Override
            public void onClick(MenuItemView view) {
                isPlaying = !isPlaying;
                if (isPlaying) {
                    startPlay();
                } else {
                    pausePlay();
                }
                if (onVideoClickListener != null) {
                    onVideoClickListener.onClickPlayIcon(isPlaying);
                }
            }
        });
        mTextureView = (KSYTextureView) view.findViewById(R.id.ksytv_player);
        rlPlayer = (RelativeLayout) findViewById(R.id.rl_player);
        rlPlayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onVideoClickListener != null) {
                    onVideoClickListener.onClickVedioArea(isPlaying);
                }
                lampItem.setVisibility(VISIBLE);
                lampItem.setOpen(isPlaying);
            }
        });
    }

    private OnVideoClickListener onVideoClickListener;

    public void setOnVideoClickListener(OnVideoClickListener onVideoClickListener) {
        this.onVideoClickListener = onVideoClickListener;
    }

    public interface OnVideoClickListener {
        void onClickVedioArea(boolean isPlaying);

        /**
         * 点击了播放控制图标
         *
         * @param isPlaying
         */
        void onClickPlayIcon(boolean isPlaying);
    }

    private void initData() {
        mTextureView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mTextureView.setOnCompletionListener(mOnCompletionListener);
        mTextureView.setOnPreparedListener(mOnPreparedListener);
        mTextureView.setOnInfoListener(mOnInfoListener);
        mTextureView.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        mTextureView.setOnErrorListener(mOnErrorListener);
        mTextureView.setOnSeekCompleteListener(mOnSeekCompletedListener);
        mTextureView.setOnMessageListener(mOnMessageListener);
        mTextureView.setScreenOnWhilePlaying(true);
        mTextureView.setKeepScreenOn(true);
        mTextureView.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_NOSCALE_TO_FIT);
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        mTextureView.start();
        lampItem.setOpen(true);
        if (onVedioPalyerListener != null) {
            onVedioPalyerListener.onStartPaly();
        }
    }
    /**
     * 暂停播放
     */
    public void pausePlay() {
        isPlaying = false;
        lampItem.setOpen(isPlaying);
        if (mTextureView != null) {
            mTextureView.pause();
        }
        if (onVedioPalyerListener != null) {
            onVedioPalyerListener.onPuase(mTextureView.getCurrentPosition());
        }
    }

    public void seekTo(long curProgress) {
        if (curProgress < 0 || curProgress > mTextureView.getDuration()) {
            return;
        }
        mTextureView.seekTo(curProgress);
    }

    /**
     * 获取截图
     *
     * @return
     */
    public Bitmap getScreenShot() {
        return mTextureView.getScreenShot();
    }

    /**
     * 重新加载新数据
     *
     * @param url
     */
    public void reload(String url) {
        this.url = url;
        lampItem.setLoadding(true);
        isPlaying = false;
        mTextureView.reload(url, false, KSYMediaPlayer.KSYReloadMode.KSY_RELOAD_MODE_FAST);
        if (onVedioPalyerListener != null) {
            onVedioPalyerListener.onReload();
        }
    }

    /**
     * 获取当前url
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置播放的地址
     *
     * @param url
     */
    public void play(String url, OnVedioPalyerListener onVedioPalyerListener) {
        this.onVedioPalyerListener = onVedioPalyerListener;
        this.onVedioPalyerListener.onStart();
        mTextureView.setLooping(isLooping);
        this.url = url;
        try {
            mTextureView.setDataSource(url);
            mTextureView.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            if (onVedioPalyerListener != null) {
                onVedioPalyerListener.onError(ErrorCode.ERROR_CODE_URL_NOT_FOUNED, e);
            }
            mTextureView.release();//回收资源
            System.gc();
            return;
        }
    }

    /**
     * 在后台运行
     *
     * @param isRunInBackground
     */
    public void runInBackground(boolean isRunInBackground) {
        mTextureView.runInBackground(isRunInBackground);
    }

    /**
     * 在前台运行
     */
    public void runInForeground() {
        mTextureView.runInForeground();
    }

    /**
     * 设置是否循环播放
     *
     * @param isLooping
     */
    public void setLooping(boolean isLooping) {
        this.isLooping = isLooping;
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        if (onVedioPalyerListener != null) {
            onVedioPalyerListener.onStop();
        }
        if (mTextureView != null) {
            mTextureView.release();
            mTextureView = null;
        }
    }

    public KSYTextureView getTextureView() {
        return mTextureView;
    }

    public long getCurrentPosition() {
        if (mTextureView != null) {
            return mTextureView.getCurrentPosition();
        }
        return 0;
    }


    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            Log.d(TAG, "OnPrepared");
            if (onVedioPalyerListener != null) {
                onVedioPalyerListener.onPrepare(mp.getDuration());
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            lampItem.setOpen(false);
                            mTextureView.pause();
                        }
                    });
                }
            }, 500);
        }
    };
    private long lastCurrentPosition = 0;
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (percent > 1) {//第一段不算
                if (lastCurrentPosition != mp.getCurrentPosition()) {
                    lastCurrentPosition = mp.getCurrentPosition();
                    if (onVedioPalyerListener != null && mTextureView != null && mTextureView.isPlaying()) {
                        onVedioPalyerListener.onPlaying(percent);
                    }
                }
            }
        }
    };

    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
//            if (mVideoWidth > 0 && mVideoHeight > 0) {
//                if (width != mVideoWidth || height != mVideoHeight) {
//                    mVideoWidth = mp.getVideoWidth();
//                    mVideoHeight = mp.getVideoHeight();
//                }
//            }
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletedListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer mp) {
//            Log.e(TAG, "onSeekComplete...............");
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
//            Toast.makeText(mContext, "OnCompletionListener, play complete.", Toast.LENGTH_LONG).show();
//            videoPlayEnd();
//            Log.e(TAG, "onCompletion: ");
            if (onVedioPalyerListener != null) {
                onVedioPalyerListener.onPlayFinished();
            }
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            if (onVedioPalyerListener != null) {
                onVedioPalyerListener.onError(ErrorCode.ERROR_CODE_URL_NOT_FOUNED, new Throwable("加载失败了"));
            }
            isPlaying = false;
            Log.e(TAG, "onCompletion: " + what);
            Log.e(TAG, "onCompletion: " + mp);
            Log.e(TAG, "onCompletion: " + extra);
            switch (what) {
                //case KSYVideoView.MEDIA_ERROR_UNKNOWN:
                // Log.e(TAG, "OnErrorListener, Error Unknown:" + what + ",extra:" + extra);
                //  break;
                default:
//                    Log.e(TAG, "OnErrorListener, Error:" + what + ",extra:" + extra);
            }

//            videoPlayEnd();

            return false;
        }
    };

    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    Log.d(TAG, "Buffering Start.");
                    Log.e(TAG, "onInfo: 193");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    Log.d(TAG, "Buffering End.");
                    Log.e(TAG, "onInfo: 197");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
//                    Toast.makeText(mContext, "Audio Rendering Start", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onInfo: 201");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                    Toast.makeText(mContext, "Video Rendering Start", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onInfo: 205");
                    break;
                case KSYMediaPlayer.MEDIA_INFO_RELOADED:
//                    Toast.makeText(mContext, "Succeed to reload video.", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "Succeed to mPlayerReload video.");
                    if (onVedioPalyerListener != null) {
                        onVedioPalyerListener.onReloadSuccess();
                    }
                    isPlaying = true;
                    lampItem.setOpen(isPlaying);
                    Log.e(TAG, "onInfo: 210");
                    return false;
            }
            return false;
        }
    };

    private IMediaPlayer.OnMessageListener mOnMessageListener = new IMediaPlayer.OnMessageListener() {
        @Override
        public void onMessage(IMediaPlayer iMediaPlayer, String name, String info, double number) {
//            Log.e(TAG, "name:" + name + ",info:" + info + ",number:" + number);
        }
    };

}
