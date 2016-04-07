package com.vincent.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/3/31.
 */
public class CircleProgressBarView extends View {
    private static final int MIN_WIDTH = 20;//控件自适应大小的时候，控件宽度，单位dp
    private static final int MIN_HEIGHT = 20;//控件自适应大小的时候，控件高度，单位dp
    private float roundRectfHeight = 2;
    private int width;//控件的宽度
    private int height;//控件的高度
    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private Canvas canvas;
    private int roundRectCount = 12;
    private int progress = 0;
    private int pointerColor = Color.parseColor("#ff000000");

    public CircleProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBarView);
        int attrCount = typedArray.getIndexCount();
        for (int i = 0; i < attrCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CircleProgressBarView_pointer_color:
                    pointerColor = typedArray.getColor(attr, pointerColor);
                    break;
                case R.styleable.CircleProgressBarView_pointer_num:
                    roundRectCount = typedArray.getInteger(attr, roundRectCount);
                    break;
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = getPaddingLeft() + getPaddingRight() + dip2px(getContext(), MIN_WIDTH);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getPaddingLeft() + getPaddingRight() + dip2px(getContext(), MIN_HEIGHT);
        }
        if (width > height) {
            width = height;
        } else {
            height = width;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        canvas.save();
        canvas.setDrawFilter(pdf);
        Path path = new Path();
        path.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CCW);//CCW 逆时针方向 CW 顺时针方向
        canvas.clipPath(path, Region.Op.REPLACE);

        canvas.translate(getWidth() / 2, getHeight() / 2);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(pointerColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1f);
        if (progress > roundRectCount) {
            progress = 1;
        }
        roundRectfHeight = (float) (2 * Math.PI * getWidth() / 4) / (float) (roundRectCount * 3);
        canvas.rotate(360 / roundRectCount * (progress++), 0f, 0f);
        canvas.save();
        for (int i = 0; i < roundRectCount; i++) {
            RectF rectF = new RectF(getWidth() / 4, -roundRectfHeight, getWidth() / 2 - 2, roundRectfHeight);
            ColorMatrix colorMatrix = new ColorMatrix(new float[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, roundRectCount / 360f * (i + 1), 0,});//透明度过滤矩阵
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawRoundRect(rectF, roundRectfHeight, roundRectfHeight, paint);
            canvas.rotate(360 / roundRectCount, 0f, 0f);
            canvas.save();
        }
        if (progress == 1) {
            handler.sendEmptyMessageDelayed(0, 100);
        }

    }

    public int dip2px(Context context, int dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            postInvalidate();
            handler.sendEmptyMessageDelayed(0, 100);
        }
    };
}
