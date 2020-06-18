package com.cymaybe.foucssurfaceview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @ClassName: Scanview
 * @Author: bsj-dingcl
 * @Email: dingchenglei@h4kit.com
 * @Date: 2020/
 * @des:
 */
public final class ScanView extends View {
    /**
     * 刷新界面的时间
     */
    private static final long ANIMATION_DELAY = 15L;
    private static final int OPAQUE = 0xFF;

    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final int SPEEN_DISTANCE = 5;

    /**
     * 手机的屏幕密度
     */
    private static float density;

    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 中间滑动线的最顶端位置
     */
    private int slideTop = 0;

    /**
     * 将扫描的二维码拍下来，这里没有这个功能，暂时不考虑
     */
    private Bitmap resultBitmap;
    private final int maskColor;
    private int mWidth;
    private int mHeight;

    boolean isFirst;
    private Rect frame;

    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;

        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.colorAccent);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //获取屏幕的宽和高
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();

        int centerX = mWidth / 2;
        int centerY = mHeight / 2;
        frame = new Rect(centerX - centerX / 5 * 4, centerY - 150, centerX + centerX / 5 * 4, centerY + 150);
        paint.setColor(maskColor);

        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        canvas.drawRect(0, 0, mWidth, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, mWidth, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, mWidth, mHeight, paint);

        paint.setColor(getResources().getColor(android.R.color.holo_orange_light));
        paint.setStrokeWidth(4);
        canvas.drawLine(frame.left, frame.top - 2, frame.left, frame.bottom, paint);
        canvas.drawLine(frame.left, frame.top, frame.right + 2, frame.top, paint);
        canvas.drawLine(frame.right, frame.top, frame.right, frame.bottom + 2, paint);
        canvas.drawLine(frame.left - 2, frame.bottom, frame.right, frame.bottom, paint);

        Rect rect1 = new Rect(frame.left, frame.top-250, frame.right, frame.bottom-250);
        Rect rect2 = new Rect(frame.left, frame.top-250+60, frame.right, frame.bottom-250+60);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        int baseLineY1 = (int) (rect1.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        int baseLineY2 = (int) (rect2.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式

        canvas.drawText("请将打码放到识别区域内",rect1.centerX(),baseLineY1,textPaint);
        canvas.drawText("拍摄一张清晰的照片",rect1.centerX(),baseLineY2,textPaint);

        //绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
        if (slideTop == 0){
            slideTop = frame.top;
        }
        slideTop += SPEEN_DISTANCE;
        if(slideTop >= frame.bottom){
            slideTop = frame.top;
        }
        Rect lineRect = new Rect();
        lineRect.left = frame.left;
        lineRect.right = frame.right;
        lineRect.top = slideTop;
        lineRect.bottom = slideTop + 18;
        canvas.drawBitmap(((BitmapDrawable)(getResources().getDrawable(R.drawable.qrcode_scan_line))).getBitmap(), null, lineRect, paint);

        //只刷新扫描框的内容，其他地方不刷新
        postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                frame.right, frame.bottom);
    }

    public Rect getFrame() {
        return frame;
    }

    public int getmHeight() {
        return mHeight;
    }

    public int getmWidth() {
        return mWidth;
    }

}
