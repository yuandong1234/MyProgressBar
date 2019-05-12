package com.yuong.progressbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MyProgressBar extends View {

    private Paint mPaintProgress;//进度条
    private Paint mPaintProgressStr;//进度数
    private Paint mPaintProgressImg;//进度图片
    private Paint mPaintBubble;//气泡
    private Paint mPaintTriangle;//三角

    private int mProgressPaintWidth = 25;//进度条的高度

    private Bitmap mProgressImg;
    private int mImgTargetSize = mProgressPaintWidth * 2;//图片是进度条宽度的2倍
    private int mBubbleHeight = mProgressPaintWidth * 3;//汽泡是进度条宽度的3倍


    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //initialize
    private void init() {
        mPaintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(mProgressPaintWidth);

        mPaintProgressImg = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaintProgressImg.setStrokeCap(Paint.Cap.ROUND);
        //mPaintProgressImg.setStyle(Paint.Style.STROKE);
        //mPaintProgressImg.setStrokeWidth(mProgressPaintWidth);

        mPaintBubble = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBubble.setStrokeCap(Paint.Cap.ROUND);
        mPaintBubble.setStyle(Paint.Style.STROKE);
        mPaintBubble.setColor(Color.parseColor("#F86F41"));

        mPaintTriangle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintTriangle.setColor(Color.parseColor("#F86F41"));


        mPaintProgressStr = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintProgressStr.setStyle(Paint.Style.STROKE);
        mPaintProgressStr.setColor(Color.parseColor("#ffffff"));
        //mPaintProgressStr.setTextSize(mTextSize);//设置字体大小
        mPaintProgressStr.setTextAlign(Paint.Align.CENTER);//将文字水平居中

        mProgressImg = measureImgSize(mImgTargetSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawBubble(canvas);
    }

    private void drawProgress(Canvas canvas) {
        //绘制进度条背景
        mPaintProgress.setColor(Color.parseColor("#FFEFDB"));
        canvas.drawLine(20, 400, 620, 400, mPaintProgress);
        //绘制进度条
        mPaintProgress.setColor(Color.parseColor("#F86F41"));
        canvas.drawLine(20, 400, 220, 400, mPaintProgress);
        //绘制图片
        canvas.drawBitmap(mProgressImg, 220 - mImgTargetSize * 0.5f, 400 - mImgTargetSize * 0.5f, mPaintProgressImg);
    }


    private void drawBubble(Canvas canvas) {

        /**
         * 气泡分两部分：一部分为圆角矩形、一部分为三角形
         * 高度比例：0.83、0.17
         */
        float rectHeight = mBubbleHeight * 0.83f;
        float rectWidth = mBubbleHeight * 2.5f;
        float triangleHeight = mBubbleHeight - rectHeight;

        mPaintBubble.setStrokeWidth(rectHeight);

        float baseY = 400 - mProgressPaintWidth*0.65f - mProgressPaintWidth * 0.5f - triangleHeight - rectHeight * 0.5f;
        Log.e("MyProgress", "base111111 : " + (baseY + rectHeight * 0.5f));
        float base2 = 400 - mProgressPaintWidth*0.65f - mProgressPaintWidth * 0.5f - triangleHeight;
        Log.e("MyProgress", "base222222 : " + base2);
        //绘制气泡矩形
        canvas.drawLine(220 - rectWidth * 0.5f + rectHeight * 0.5f, baseY, 220 + rectWidth * 0.5f - rectHeight * 0.5f, baseY, mPaintBubble);

        //绘制气泡三角形
        float triangleWidth = (float) (triangleHeight * Math.tan(Math.toRadians(30)));
        Path path = new Path();
        path.moveTo(220 - triangleWidth, (int) base2);
        path.lineTo(220 + triangleWidth, (int) base2);
        path.lineTo(220, base2 + triangleHeight);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, mPaintTriangle);

        //绘制文字
        mPaintProgressStr.setTextSize(rectHeight / 2);
        String text = "3500";
        int textHeight = measureTextHeight(text, mPaintProgressStr);
        canvas.drawText("3500", 220, baseY + textHeight * 0.5f, mPaintProgressStr);

    }


    //图片转化为目标的大小
    private Bitmap measureImgSize(float targetSize) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress);
        Matrix matrix = new Matrix();
        float scaleWidth = targetSize * 1.0f / bitmap.getWidth();
        float scaleHeight = targetSize * 1.0f / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    //获取文本的高度
    private int measureTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        //int width = rect.width();
        return rect.height();
    }

}
