package com.example.hpf.wheeloffortune;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by HPF on 2016/6/17.
 */
public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread thread;
    private boolean isRunning;

    private List<String> mNameList;

//    private int[] mImgs = new int[]{
//            R.drawable.danfan, R.drawable.ipad, R.drawable.f040, R.drawable.iphone, R.drawable.meizi, R.drawable.f015
//    };

    private int[] mColors = new int[]{
            0xFFFFC300, 0XFFF17E01
    };

    private int mItemCount ;
//    private Bitmap[] mImagsBitmap;

    private RectF mRange = new RectF();
    private int mRadius;

    private Paint mArcPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;

    private double mSpeed;
    private volatile float mStartAngle = 0;

    private boolean isStart;

    private int mCenter;
    private int mPadding;

    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    public LuckyPan(Context context) {
        this(context, null, null);
    }

    public LuckyPan(Context context, AttributeSet attrs, List<String> namesList) {
        super(context, attrs);
        this.mNameList = namesList;
        mItemCount = namesList.size();
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    public LuckyPan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        Log.i("width", width+"");

        mPadding = getPaddingLeft();
        Log.i("padding", mPadding+"");
        mRadius = width - mPadding*2;
        mCenter = mPadding+mRadius/2;
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        mLinePaint = new Paint();
        mLinePaint.setColor(0xffffffff);
        mLinePaint.setStrokeWidth(10);
        mLinePaint.setTextSize(mTextSize);

        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);

//        mImagsBitmap = new Bitmap[mItemCount];
//        for (int i = 0; i < mItemCount; i++) {
//            mImagsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
//        }

        isRunning = true;
        thread = new Thread(this);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        isRunning = false;

    }

    @Override
    public void run() {

        while (isRunning) {

            long start = System.currentTimeMillis();

            draw();

            long end = System.currentTimeMillis();

            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void draw() {

        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                drawBg();

                float temAngle = mStartAngle;
                float sweepAngle = 360 /mItemCount;
                for (int i= 0; i<mItemCount; i++) {
                    mArcPaint.setColor(mColors[i%2]);
                    mCanvas.drawArc(mRange, temAngle, sweepAngle, true, mArcPaint);
                    drawText(temAngle, sweepAngle, mNameList.get(i));
                    drawLine(temAngle);
                    temAngle += sweepAngle;
                }

                mStartAngle += mSpeed;

                if (isStart){
                    mSpeed -= 1;
                }
                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isStart = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
            
        }

    }

    public void luckyStart(){


//        float angle = 360/mItemCount;
//        float from = 270 - (index + 1) * angle;
//
//        float targetFrom = 4*360+from;
//        float targetEnd = targetFrom + angle;
//
//        float v1 = (float) ((-1+Math.sqrt(1+8*targetFrom))/2);
//        float v2 = (float) ((-1+Math.sqrt(1+8*targetEnd))/2);

        mStartAngle = 0;
        mSpeed = Math.random()*100;
        isStart = true;
//        isEnd = false;
    }

//    public void luckyEnd(){
//        isEnd = true;
//
//    }

    public boolean isStart(){
        return isStart;
    }

//    public boolean isEnd(){
//        return isEnd;
//    }

    private void drawLine(float temAngle) {

        float angle = (float) (temAngle*Math.PI/180);

        int x = (int) (mCenter + mRadius/2*Math.cos(angle));
        int y = (int) (mCenter + mRadius/2*Math.sin(angle));
        mCanvas.drawLine(mCenter, mCenter, x, y, mLinePaint);

    }

    private void drawText(float temAngle, float sweepAngle, String string) {

        Path path = new Path();
        path.addArc(mRange, temAngle, sweepAngle);
        float textWidth = mTextPaint.measureText(string);
        int hOffset = (int) (mRadius*Math.PI/mItemCount/2 - textWidth/2);
        int vOffset = mRadius/2/6;
        mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
    }

    private void drawBg() {
        mCanvas.drawColor(0xFFFFFFFF);
        mCanvas.drawBitmap(mBgBitmap, null,
                new Rect(mPadding/2, mPadding/2, getMeasuredWidth() - mPadding/2, getMeasuredWidth() - mPadding/2),
                null);
        Log.i("width", getMeasuredWidth()+"");
    }
}
