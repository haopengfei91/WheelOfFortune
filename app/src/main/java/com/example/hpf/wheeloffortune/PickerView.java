package com.example.hpf.wheeloffortune;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HPF on 2016/6/18.
 */
public class PickerView extends View {

    public static final String TAG = "PickerView";
    public static final float MARGIN_ALPHA = 2.8f;
    public static final float SPEED = 100;
    private List<String> mData;

    private int mCurrentSelected;
    private Paint mPaint;

    private float mMaxTextSize = 40;
    private float mMinTextSize = 20;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;

    private int mColorText = 0x333333;

    private int mViewHeight;
    private int mViewWidth;

    private float mLastDownY;

    private float mMoveLen = 0;
    private boolean isInit = false;
    private OnSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;

    Handler updateHandler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
//            if (Math.abs(mMoveLen) < SPEED)
//            {
//                mMoveLen = 0;
//                if (mTask != null)
//                {
//                    mTask.cancel();
//                    mTask = null;
//                    performSelect();
//                }
//            } else
//                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
//                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            invalidate();
        }

    };

//    private void performSelect()
//    {
////        if (mSelectListener != null)
//            mSelectListener.onSelect(mData.get(mCurrentSelected));
//    }



    public PickerView(Context context) {
        this(context, null);
    }

    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void moveHeadToTail()
    {
        String head = mData.get(0);
        mData.remove(0);
        mData.add(head);
    }

    private void moveTailToHead()
    {
        String tail = mData.get(mData.size() - 1);
        mData.remove(mData.size() - 1);
        mData.add(0, tail);
    }


    private void init() {
        timer = new Timer();
        mData = new ArrayList<String>();
        for (int i=1; i<=10; i++) {
            mData.add(i+"");
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mColorText);
        setSelected(5);
    }

    public void setSelected(int selected)
    {
        mCurrentSelected = selected;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        // 按照View的高度计算字体大小
        mMaxTextSize = mViewHeight / 6.0f;
        mMinTextSize = mMaxTextSize / 2.0f;
        isInit = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInit) {
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        float scale = parabola(mMaxTextSize, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mData.get(mCurrentSelected), x, baseline, mPaint);
        // 绘制上方data
        for (int i = 1; i<=mCurrentSelected; i++)
        {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelected + i) < mData.size(); i++)
        {
            drawOtherText(canvas, i, 1);
        }


    }

    private float parabola(float zero, float x)
    {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    private void drawOtherText(Canvas canvas, int position, int type)
    {
        float d =(MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveLen);
        float scale = parabola(mMaxTextSize, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        Paint.FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mData.get(mCurrentSelected + type * position),
                (float) (mViewWidth / 2.0), baseline, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                    doMove(event);


                break;
            case MotionEvent.ACTION_UP:

                    doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event)
    {
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event)
    {
            mMoveLen += (event.getY() - mLastDownY);

            if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2)
            {
                // 往下滑超过离开距离
            moveTailToHead();
                mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;


            } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2)
            {
                // 往上滑超过离开距离
            moveHeadToTail();
                mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;


            }
            mLastDownY = event.getY();


        Log.i("movelen", mMoveLen+"");


//        invalidate();

        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 100);
        Log.i("current", mCurrentSelected+"");
    }

    private void doUp(MotionEvent event)
    {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < SPEED)
        {
            mMoveLen = 0;
        } else {
            mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
        }
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;

        }

        invalidate();

        Log.i("data", mData.get(mCurrentSelected)+"");
        mSelectListener.onSelect(mData.get(mCurrentSelected));
//        mTask = new MyTimerTask(updateHandler);
//        timer.schedule(mTask, 0, 1000);
    }

    class MyTimerTask extends TimerTask
    {
        Handler handler;

        public MyTimerTask(Handler handler)
        {
            this.handler = handler;
        }

        @Override
        public void run()
        {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    public interface OnSelectListener
    {
        void onSelect(String text);
    }

    public void setOnSelectListener(OnSelectListener listener) {
        mSelectListener = listener;
    }


}
