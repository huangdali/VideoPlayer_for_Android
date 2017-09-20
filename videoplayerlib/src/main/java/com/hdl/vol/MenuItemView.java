package com.hdl.vol;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 菜单子项
 * Created by dali on 2017/4/24.
 */

public class MenuItemView extends android.support.v7.widget.AppCompatImageView implements Comparable<MenuItemView> {
    /**
     * 正常状态
     */
    private static final int STATE_NORMAL = 1;
    /**
     * 正常按下时
     */
    private static final int STATE_NORMAL_PRESS = 2;
    /**
     * 正在加载
     */
    private static final int STATE_LODDING = 3;
    /**
     * 选中状态
     */
    private static final int STATE_CHECKED = 4;
    /**
     * 选中按下时
     */
    private static final int STATE_CHECKED_PRESS = 5;
    /**
     * 本view的宽高
     */
    private int widthSize = 46, heightSize = 46;
    /**
     * 外底部边距
     */
    private int marginBottom = 0;
    /**
     * 显示位置
     */
    private int showPosition;
    /**
     * 功能类型
     */
    private int funType;

    /**
     * normalResId       正常显示图片 <br />
     * normalPressResId  按下的时候显示的图片 <br />
     * loaddingResId     加载的时候显示的图片 <br />
     * checkedResId      最后显示的图片 <br />
     * checkedPressResId 最后选中的图片的按下状态 <br />
     */
    private int normalResId, normalPressResId, loaddingResId, checkedResId, checkedPressResId;
    /**
     * 是否选中[是否打开--默认是关闭的]
     */
    private boolean isOpen;
    /**
     * 是否正在加载中
     */
    private boolean isLoadding = true;
    /**
     * 设置是否显示圆形背景（不设置就默认为显示）
     */
    private boolean isShowCircleBg = true;

    private OnClickMenuItemViewListener onClickMenuItemViewListener;

    private ObjectAnimator loaddingAnimator = ObjectAnimator.ofFloat(this, "Rotation", 0, 120, 360);

    private Timer hideTimer;//定时隐藏

    public MenuItemView(Context context) {
        this(context, null);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER_INSIDE);
        widthSize = DensityUtil.dip2px(context, widthSize);
        heightSize = DensityUtil.dip2px(context, heightSize);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(widthSize, heightSize);
        layoutParams.setMargins(0, 0, 0, DensityUtil.dip2px(context, marginBottom));
        this.setLayoutParams(layoutParams);
        initAnimation();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickUpdateState(v);
            }
        });
    }

    /**
     * 更新状态
     *
     * @param v
     */
    public void onClickUpdateState(View v) {
        setLoadding(true);//点击的时候默认是加载状态
        if (onClickMenuItemViewListener != null) {
            onClickMenuItemViewListener.onClick((MenuItemView) v);
        } else {
            setLoadding(false);//没有设置监听就停止显示动画
        }
        if (checkedResId == 0) {//没有选中状态
            setState(STATE_NORMAL);
        }
        if (loaddingResId == 0 && checkedResId != 0) {//简单开关类型,不同等待处理结果就可以操作开关
            isOpen = !isOpen;
            setOpen(isOpen);
        }
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        loaddingAnimator.setDuration(1000);//设置旋转
        loaddingAnimator.setRepeatCount(100);//重复
        loaddingAnimator.setInterpolator(new LinearInterpolator());
    }


    /**
     * 设置图片资源id
     *
     * @param normalResId       正常显示图片
     * @param normalPressResId  按下的时候显示的图片
     * @param loaddingResId     加载的时候显示的图片
     * @param checkedResId      最后显示的图片
     * @param checkedPressResId 最后选中的图片的按下状态
     */
    public void buildSwitchLoaddingType(int normalResId, int normalPressResId, int checkedResId, int checkedPressResId, int loaddingResId) {
        this.normalResId = normalResId;
        this.normalPressResId = normalPressResId;
        this.loaddingResId = loaddingResId;
        this.checkedResId = checkedResId;
        this.checkedPressResId = checkedPressResId;
        setState(STATE_NORMAL);
    }

    /**
     * 设置图片资源id
     *
     * @param normalResId       正常显示图片
     * @param normalPressResId  按下的时候显示的图片
     * @param checkedResId      最后显示的图片
     * @param checkedPressResId 最后选中的图片的按下状态
     */
    public void buildSwitchType(int normalResId, int normalPressResId, int checkedResId, int checkedPressResId) {
        this.normalResId = normalResId;
        this.normalPressResId = normalPressResId;
        this.checkedResId = checkedResId;
        this.checkedPressResId = checkedPressResId;
        setState(STATE_NORMAL);
    }

    /**
     * 设置图片资源id
     *
     * @param normalResId      正常显示图片
     * @param normalPressResId 按下的时候显示的图片
     * @param checkedResId     最后显示的图片
     * @deprecated 过时，不建议使用
     */
    public void setImageResIds(int normalResId, int normalPressResId, int checkedResId) {
        this.normalResId = normalResId;
        this.normalPressResId = normalPressResId;
        this.checkedResId = checkedResId;
        setState(STATE_NORMAL);
    }

    /**
     * 设置图片资源id
     *
     * @param normalResId      正常显示图片
     * @param normalPressResId 按下的时候显示的图片
     */
    public void buildDefualtType(int normalResId, int normalPressResId) {
        this.normalResId = normalResId;
        this.normalPressResId = normalPressResId;
        setState(STATE_NORMAL);
    }

    /**
     * 处理触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLoadding) {//加载的是否不能点击（点击无效）
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN://按下了要显示按下时的图片
                    if (checkedResId == 0) {//判断有没有选中状态
                        setState(STATE_NORMAL_PRESS);
                    } else {//有
                        if (isOpen) {//判断是否已经选中了
                            setState(STATE_CHECKED_PRESS);
                        } else {//没有选中
                            setState(STATE_NORMAL_PRESS);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP://抬起了，要恢复
                    if (checkedResId == 0) {///判断有没有选中状态
                        setState(STATE_NORMAL);
                    } else {
                        if (isOpen) {
                            setState(STATE_CHECKED);
                        } else {
                            setState(STATE_NORMAL);
                        }
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (checkedResId == 0) {///判断有没有选中状态
                        setState(STATE_NORMAL);
                    } else {
                        if (isOpen) {
                            setState(STATE_CHECKED);
                        } else {
                            setState(STATE_NORMAL);
                        }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 设置是否选中
     *
     * @param open
     */
    public void setOpen(boolean open) {
        this.isLoadding = false;//调用setchecked说明已经有结果了，就不在加载了
        this.isOpen = open;
        if (isOpen) {
            if (hideTimer != null) {
                hideTimer.cancel();
                hideTimer=null;
            }
            hideTimer = new Timer();
            hideTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (getVisibility()==VISIBLE) {
                                setVisibility(GONE);
                            }
                        }
                    });
                }
            }, 2000);
            setState(STATE_CHECKED);
        } else {
            if (hideTimer != null) {
                hideTimer.cancel();
                hideTimer=null;
            }
            if (getVisibility()==GONE) {
                setVisibility(VISIBLE);
            }
            setState(STATE_NORMAL);
        }
    }

    public boolean isLoadding() {
        return isLoadding;
    }

    /**
     * 设置是否正在加载中
     *
     * @param loadding
     */
    public void setLoadding(boolean loadding) {
        isLoadding = loadding;
        if (isLoadding) {
            setState(STATE_LODDING);
        } else {
            setState(isOpen ? STATE_CHECKED : STATE_NORMAL);//判断是否选中了
        }
    }

    /**
     * 设置单击事件
     *
     * @param onClickMenuItemViewListener
     */
    public void setOnClickMenuItemViewListener(OnClickMenuItemViewListener onClickMenuItemViewListener) {
        this.onClickMenuItemViewListener = onClickMenuItemViewListener;
    }

    /**
     * 排序
     *
     * @param old
     * @return
     */
    @Override
    public int compareTo(MenuItemView old) {
        return old.getShowPosition() > showPosition ? -1 : 1;
    }

    public interface OnClickMenuItemViewListener {
        void onClick(MenuItemView view);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);//设置测量的宽度
    }


    /**
     * 设置状态
     *
     * @param state
     */
    private void setState(int state) {
        setClickable(true);
        setEnabled(true);
        closeLoaddingAnimator();//关闭动画
        switch (state) {
            case STATE_NORMAL:
                setImageResource(normalResId);
                break;
            case STATE_NORMAL_PRESS:
                setImageResource(normalPressResId);
                break;
            case STATE_LODDING:
                setImageResource(loaddingResId);
                loaddingAnimator.start();
                setClickable(false);
                setEnabled(false);
                break;
            case STATE_CHECKED:
                setImageResource(checkedResId);
                break;
            case STATE_CHECKED_PRESS:
                setImageResource(checkedPressResId);
                break;
        }
    }

    /**
     * 关闭动画
     */
    private void closeLoaddingAnimator() {
        if (loaddingAnimator.isRunning()) {
            loaddingAnimator.end();
        }
    }

    public boolean isShowCircleBg() {
        return isShowCircleBg;
    }

    /**
     * 设置是否显示圆形背景
     *
     * @param showCircleBg
     */
    public void setShowCircleBg(boolean showCircleBg) {
        isShowCircleBg = showCircleBg;
        if (!isShowCircleBg) {//根据调用者来决定是否使用圆形背景
            if (Build.VERSION.SDK_INT >= 16) {
                this.setBackground(null);//把之前设置的背景覆盖掉
            } else {
                this.setBackgroundDrawable(null);
            }
        }
    }

    public int getShowPosition() {
        return showPosition;
    }

    /**
     * 设置显示位置--用于排序
     *
     * @param showPosition
     * @return
     */
    public MenuItemView setShowPosition(int showPosition) {
        this.showPosition = showPosition;
        return this;
    }

    public int getFunType() {
        return funType;
    }

    public void setFunType(int funType) {
        this.funType = funType;
    }

    /**
     * 功能类型
     */
    public class FunctionType {
        public static final int MEMORY_POINT = 0;
        public static final int CONTROL_SENSOR = 1;
        public static final int OPEN_DOOR = 2;
        public static final int LAMP = 3;
    }
}
