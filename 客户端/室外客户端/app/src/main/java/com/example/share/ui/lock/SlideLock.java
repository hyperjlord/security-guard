package com.example.share.ui.lock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.share.R;

import androidx.annotation.Nullable;

public class SlideLock extends View {

    private Bitmap jiesuo_bg;
    private Bitmap jiesuo_button;
    private int bg_width;
    private int bg_hight;
    private int block_width;
    private int measuredWidth;
    private int measuredHeight;
    private float currentX;
    private float currentY;
    private int left;
    private int right;
    private float downX;
    private float downY;
    private boolean isOnBlock;
    private OnUnlockListener onUnlockListener;
    public SlideLock(Context context) {
        super(context);
        initView();
    }

    public SlideLock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SlideLock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    private void initView(){
        jiesuo_bg = BitmapFactory.decodeResource(getResources(), R.drawable.jiesuo_bg);
        jiesuo_button = BitmapFactory.decodeResource(getResources(), R.drawable.jiesuo_button);
        bg_width = jiesuo_bg.getWidth();
        bg_hight = jiesuo_bg.getHeight();
        block_width = jiesuo_button.getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(jiesuo_bg,measuredWidth/2-bg_width/2,measuredHeight/2-bg_hight/2,null);
        //控制边界
        if (currentX<left){
            currentX=left;
        }else if (currentX>right){
            currentX=right;
        }
        canvas.drawBitmap(jiesuo_button,currentX,currentY,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        //获取一开始的位置
        currentX = measuredWidth / 2 - bg_width / 2;
        currentY = measuredHeight / 2 - bg_hight / 2;
        left = measuredWidth / 2 - bg_width / 2;
        right = measuredWidth / 2 + bg_width / 2 - block_width;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //判断手指是否按在了小球上
                downX = event.getX();
                downY = event.getY();
                isOnBlock = isOnBlock(downX, downY);
                if (isOnBlock){
                   // Toast.makeText(getContext(),"按到了",Toast.LENGTH_SHORT).show();;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isOnBlock){
                    float moveX = event.getX();
                    currentX = moveX - block_width / 2;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isOnBlock=false;
                if (currentX<right-5){
                    currentX=left;
                }else {
                    if (onUnlockListener!=null){
                       // Toast.makeText(getContext(),"解锁",Toast.LENGTH_SHORT).show();
                        onUnlockListener.setUnlock(true);
                    }
                }
                invalidate();
                break;
        }
        return true;
    }
    private boolean isOnBlock(float downX,float downY){
        float rX= currentX + block_width / 2;
        float rY=currentY+block_width/2;
        double distance = Math.sqrt((downX - rX) * (downX - rX) + (downY - rY) * (downY - rY));
        if (distance<block_width/2){
            return true;
        }
        return false;
    }

    public void setOnUnlockListener(OnUnlockListener onUnlockListener){
        this.onUnlockListener=onUnlockListener;
    }
}

