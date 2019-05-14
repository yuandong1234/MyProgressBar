package com.yuong.progressbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


public class MyProgressBar extends View {

    /**
     * paint
     */
    private Paint mProgressPaint;
    private Paint mProgressStrPaint;
    private Paint mProgressImgPaint;
    private Paint mBubblePaint;
    private Paint mTrianglePaint;
    private int mProgressBarColor;
    private int mProgressBarBackgroundColor;
    private int mProgressBarTextColor;

    /**
     * progressbar
     */
    private float mProgressBarX;
    private float mProgressBarY;
    private int mProgressPaintWidth;

    /**
     * image
     */
    private Bitmap mProgressImg;
    private int mImgTargetSize;
    private int mProgressBarIcon;

    /**
     * bubble
     */
    private int mBubbleHeight;
    private float mRectHeight;
    private float mRectWidth;
    private float mTriangleHeight;
    private float mRectY;
    private float mTriangleY;


    private int mWidth;
    private int mHeight;
    private float mHorizontalPadding;
    private float mVerticalPadding;

    private float mProgress;
    private String mProgressStr;

    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    //initialize
    private void init(Context context, AttributeSet attrs) {

        initAttrs(context, attrs);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setDither(true);
        mProgressPaint.setStrokeWidth(mProgressPaintWidth);

        mProgressImgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBubblePaint.setStrokeCap(Paint.Cap.ROUND);
        mBubblePaint.setStyle(Paint.Style.STROKE);
        mBubblePaint.setColor(mProgressBarColor);

        mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrianglePaint.setColor(mProgressBarColor);

        mProgressStrPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressStrPaint.setStyle(Paint.Style.STROKE);
        mProgressStrPaint.setColor(mProgressBarTextColor);
        mProgressStrPaint.setTextAlign(Paint.Align.CENTER);

        initViewSize();
    }

    //get custom view attributes
    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyProgressBar);

        float horizontalPadding = typedArray.getDimension(R.styleable.MyProgressBar_horizontal_padding, 10);
        float verticalPadding = typedArray.getDimension(R.styleable.MyProgressBar_vertical_padding, 10);
        float progressbarWidth = typedArray.getDimension(R.styleable.MyProgressBar_progressbar_width, 8);
        mProgressBarColor = typedArray.getColor(R.styleable.MyProgressBar_progressbar_color, Color.parseColor("#F86F41"));
        mProgressBarBackgroundColor = typedArray.getColor(R.styleable.MyProgressBar_progressbar_background_color, Color.parseColor("#FFEFDB"));
        mProgressBarTextColor = typedArray.getColor(R.styleable.MyProgressBar_progressbar_text_color, Color.WHITE);
        mProgressBarIcon = typedArray.getResourceId(R.styleable.MyProgressBar_progressbar_icon, 0);

        mProgressPaintWidth = dp2px(context, progressbarWidth);
        mHorizontalPadding = dp2px(context, horizontalPadding);
        mVerticalPadding = dp2px(context, verticalPadding);
        typedArray.recycle();
    }


    private void initViewSize() {
        /**
         * 图片的高度是进度条宽度的2倍
         * 气泡的高度是进度条宽度的3倍
         * 气泡分两部分：一部分为圆角矩形、一部分为三角形(高度比例：0.83、0.17)
         * 气泡的宽度是进度条宽度的3倍
         * 三角箭头到图片的距离为进度条宽度的0.65
         */
        mImgTargetSize = mProgressPaintWidth * 2;
        mBubbleHeight = mProgressPaintWidth * 3;
        mRectHeight = mBubbleHeight * 0.83f;
        mRectWidth = mBubbleHeight * 2.5f;
        mTriangleHeight = mBubbleHeight - mRectHeight;
        float mImgMarginTop = mProgressPaintWidth * 0.65f;

        mHeight = (int) (mVerticalPadding * 2f + mProgressPaintWidth + mBubbleHeight + mImgMarginTop);
        mProgressBarY = mVerticalPadding + mProgressPaintWidth * 0.5f + mBubbleHeight + mImgMarginTop;
        mRectY = mProgressBarY - mProgressPaintWidth * 0.5f - mImgMarginTop - mTriangleHeight - mRectHeight * 0.5f;
        mTriangleY = mProgressBarY - mProgressPaintWidth * 0.5f - mImgMarginTop - mTriangleHeight;

        mProgressImg = measureImgSize(mImgTargetSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawBubble(canvas);
    }

    private void drawProgress(Canvas canvas) {
        float padding = mHorizontalPadding + mRectWidth * 0.5f;
        //draw  progress bar background
        mProgressPaint.setColor(mProgressBarBackgroundColor);
        canvas.drawLine(padding, mProgressBarY, mWidth - padding, mProgressBarY, mProgressPaint);
        //draw progress bar
        mProgressPaint.setColor(mProgressBarColor);
        float realLength = getRealProgressLength(mProgress);
        mProgressBarX = padding + realLength;
        canvas.drawLine(padding, mProgressBarY, mProgressBarX, mProgressBarY, mProgressPaint);
        //draw  picture
        canvas.drawBitmap(mProgressImg, mProgressBarX - mImgTargetSize * 0.5f, mProgressBarY - mImgTargetSize * 0.5f, mProgressImgPaint);
    }


    private void drawBubble(Canvas canvas) {
        if (TextUtils.isEmpty(mProgressStr)) {
            return;
        }
        //draw bubble
        mBubblePaint.setStrokeWidth(mRectHeight);
        canvas.drawLine(mProgressBarX - mRectWidth * 0.5f + mRectHeight * 0.5f, mRectY, mProgressBarX + mRectWidth * 0.5f - mRectHeight * 0.5f, mRectY, mBubblePaint);

        //draw triangle
        float triangleWidth = (float) (mTriangleHeight * Math.tan(Math.toRadians(30)));
        Path path = new Path();
        path.moveTo(mProgressBarX - triangleWidth, (int) mTriangleY);
        path.lineTo(mProgressBarX + triangleWidth, (int) mTriangleY);
        path.lineTo(mProgressBarX, mTriangleY + mTriangleHeight);
        path.close();
        canvas.drawPath(path, mTrianglePaint);

        //draw txt
        mProgressStrPaint.setTextSize(mRectHeight / 2);
        if (!TextUtils.isEmpty(mProgressStr)) {
            int textHeight = measureTextHeight(mProgressStr, mProgressStrPaint);
            canvas.drawText(mProgressStr, mProgressBarX, mRectY + textHeight * 0.5f, mProgressStrPaint);
        }
    }


    /**
     * image converted to target size
     *
     * @param targetSize
     * @return
     */
    private Bitmap measureImgSize(float targetSize) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_progress);
        Matrix matrix = new Matrix();
        float scaleWidth = targetSize * 1.0f / bitmap.getWidth();
        float scaleHeight = targetSize * 1.0f / bitmap.getHeight();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Get the height of the text
     *
     * @param text
     * @param paint
     * @return
     */
    private int measureTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        //int width = rect.width();
        return rect.height();
    }


    private float getRealProgressLength(float currentProgress) {
        int totalLength = (int) (mWidth - 2 * mHorizontalPadding - mRectWidth);
        //TODO 根据实际项目，设置进度条的相对范围
        /**
         * 进度条范围：0 ~ 100
         */
        return currentProgress * totalLength * 0.01f;
    }

    /**
     * set progress
     *
     * @param progress
     */
    public void setProgress(float progress) {
        mProgress = progress;
        mProgressStr = (int) (progress) + "%";
        invalidate();
    }

    /**
     * set animation progress
     */
    public void setProgressWithAnim(float progress) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 0, progress);
        animator.setDuration(1000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    /**
     * dp to  px
     */
    private static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
