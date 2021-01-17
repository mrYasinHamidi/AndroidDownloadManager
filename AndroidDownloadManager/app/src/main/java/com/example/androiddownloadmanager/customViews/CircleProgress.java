package com.example.androiddownloadmanager.customViews;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.ColorInt;

import com.example.androiddownloadmanager.R;


public class CircleProgress extends View {

    private int min = 0;
    private int max = 100; // default value
    private int progress = 0;
    private int color = Color.DKGRAY;
    private float strokeWidth = 4;

    private Paint backgroundPaint;
    private Paint foregroundPaint;
    private RectF rectF;
    private Rect bounds;
    private Paint percentPaint;

    private boolean autoColored = false;
    private boolean showPercent = true;
    public CircleProgress(Context context) {
        super(context);
        init(context);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // attrs
        TypedArray ta = context.
                obtainStyledAttributes(attrs, R.styleable.CircleProgress, defStyleAttr, 0);
        min = ta.getInteger(R.styleable.CircleProgress_cp_min, 0);
        max = ta.getInteger(R.styleable.CircleProgress_cp_max, 100);
        if(max < min)
            max = min;
        progress = ta.getInteger(R.styleable.CircleProgress_cp_progress, min);
        if(progress < min){
            progress = min;
        } else if(progress > max) {
            progress = max;
        }

        color = ta.getColor(R.styleable.CircleProgress_cp_color, Color.DKGRAY);
        strokeWidth = ta.getDimension(R.styleable.CircleProgress_cp_stroke_width, 4);
        autoColored = ta.getBoolean(R.styleable.CircleProgress_cp_auto_colored, false);
        showPercent = ta.getBoolean(R.styleable.CircleProgress_cp_show_percent, true);
        ta.recycle();
        // initialize
        init(context);
    }

    private void init(Context context){
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(adjustAlpha(color, 0.15f));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(strokeWidth);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setColor(color);
        foregroundPaint.setStyle(Paint.Style.STROKE);
        foregroundPaint.setStrokeWidth(strokeWidth);

        rectF = new RectF();
        bounds = new Rect();
        percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        percentPaint.setColor(color);
        percentPaint.setTextAlign(Paint.Align.CENTER);
        percentPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        float density = context.getResources().getDisplayMetrics().density;
        percentPaint.setTextSize(20 * density);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(strokeWidth/2, strokeWidth/2, min - strokeWidth/2, min - strokeWidth/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int percent = (progress - min) * 100 / (max - min);
        if(autoColored){
            int r = (100 - percent) * 2 ;
            int g = percent * 2;
            int b = 20;
            int newColor = Color.rgb(r, g, b);
            backgroundPaint.setColor(adjustAlpha(newColor, 0.2f));
            foregroundPaint.setColor(newColor);
            percentPaint.setColor(newColor);
        } else {
            backgroundPaint.setColor(adjustAlpha(color, 0.2f));
            foregroundPaint.setColor(color);
            percentPaint.setColor(color);
        }

        canvas.drawOval(rectF, backgroundPaint);

        int sweepAngel = (progress - min) * 360 / (max - min);
        canvas.drawArc(rectF, -90, sweepAngel, false, foregroundPaint);

        if(showPercent){
            String percentLabel = percent + " %";
            float x = getPaddingLeft() + (getWidth() - getPaddingLeft() - getPaddingRight()) / 2;
            float y = getPaddingTop() + (getHeight() - getPaddingTop() - getPaddingBottom()) / 2;
            percentPaint.getTextBounds(percentLabel, 0, percentLabel.length(), bounds);
            y += bounds.height() / 2;
            canvas.drawText(percentLabel, x, y, percentPaint);
        }

    }

    public void setMin(int min){
        this.min  = (min > 0 ) ? min : 0;
        invalidate();
    }

    public void setMax(int max){
        this.max = (max > min) ? max : min;
        invalidate();
    }

    public void setProgress(int value){
        if(value < min) {
            this.progress = min;
        } else if(value > max) {
            this.progress = max;
        } else {
            this.progress = value;
        }
        invalidate();
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public void setStrokeWidth(float strokeWidth) {
        if(strokeWidth < 0 || strokeWidth == this.strokeWidth )
            return;
        this.strokeWidth = strokeWidth;
        foregroundPaint.setStrokeWidth(strokeWidth);
        backgroundPaint.setStrokeWidth(strokeWidth);
        invalidate();
        requestLayout();
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        foregroundPaint.setColor(color);
        backgroundPaint.setColor(adjustAlpha(color, 0.2f));
        invalidate();
    }

    public boolean isAutoColored() {
        return autoColored;
    }

    public void setAutoColored(boolean autoColored) {
        if(this.autoColored == autoColored)
            return;
        this.autoColored = autoColored;
        invalidate();
    }

    public boolean isShowPercent() {
        return showPercent;
    }

    public void setShowPercent(boolean showPercent) {
        if(this.showPercent == showPercent)
            return;
        this.showPercent = showPercent;
        invalidate();
    }

    // factor 0.0f to 1.0f
    private int adjustAlpha(int color, float factor){
        if(factor < 0.0f || factor > 1.0f)
            return color;

        float alpha = Math.round(Color.alpha(color) * factor);
        return Color.argb(
                (int) alpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    public void setProgressWithAnimation(int value){
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "progress", value);
        long duration = Math.abs(value - progress) * 2000L / (max - min);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}
