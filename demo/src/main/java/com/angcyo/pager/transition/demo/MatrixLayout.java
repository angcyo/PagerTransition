package com.angcyo.pager.transition.demo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/03/22
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class MatrixLayout extends FrameLayout {

    Matrix matrix = new Matrix();
    /**
     * 视图轮廓对应的矩形
     */
    RectF viewRectF = new RectF();
    /**
     * 拖拽后, 绘制的矩形
     */
    RectF drawRectF = new RectF();

    /**
     * 允许最大放大到多少倍
     */
    float maxScale = 1f;
    /**
     * 允许最小缩放到多少倍
     */
    float minScale = 0.4f;

    /**
     * 最小y轴移动多少, 控制手指向上滑动时, 是否可以y轴移动
     */
    float minTranslateY = 0;

    GestureDetectorCompat gestureDetectorCompat;

    OnMatrixTouchListener onMatrixTouchListener;

    public MatrixLayout(Context context) {
        this(context, null);
    }

    public MatrixLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatrixLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context, attrs, defStyleAttr);
    }

    protected void initLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        gestureDetectorCompat = new GestureDetectorCompat(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        //L.e("dx:" + distanceX + " dy:" + distanceY /*+ " " + checkTouchEvent + " " + matrixChange*/);

                        if (isMatrixChange()) {
                            doOnScroll(e1, e2, distanceX, distanceY);
                            return true;
                        } else {
                            if (distanceY < 0) {
                                //手指向下滑动
                                if (Math.abs(distanceY) > Math.abs(distanceX)) {
                                    doOnScroll(e1, e2, distanceX, distanceY);
                                    return true;
                                }
                            }
                            return false;
                        }
                    }
                });
    }

    private void doOnScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e1 == null || e2 == null) {
            //在某些情况下, 这玩意竟然会为空
            return;
        }

        float moveY = e2.getY() - e1.getY();
        float scale = (getMeasuredHeight() - e2.getY()) / (getMeasuredHeight() - e1.getY());
        if (scale > maxScale) {
            scale = maxScale;
        } else if (scale < minScale) {
            scale = minScale;
        }
        setMatrix(scale, e2.getX() - e1.getX(), Math.max(moveY, minTranslateY));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewRectF.set(0, 0, w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(matrix);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (needTouchEvent()) {
            return gestureDetectorCompat.onTouchEvent(ev);
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (checkTouchEvent() || isMatrixChange()) {
            gestureDetectorCompat.onTouchEvent(event);

            int actionMasked = event.getActionMasked();

            if (actionMasked == MotionEvent.ACTION_UP ||
                    actionMasked == MotionEvent.ACTION_CANCEL) {

                if (onMatrixTouchListener != null &&
                        onMatrixTouchListener.onTouchEnd(this,
                                new Matrix(matrix),
                                new RectF(viewRectF),
                                new RectF(drawRectF))) {
                    //nothing
                } else {
                    reset(drawRectF);
                }
            }
            return true;
        } else {
            return result;
        }
    }

    private void reset(RectF fromRectF) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(300);

        final float startScale = fromRectF.width() / viewRectF.width();
        final float startTranX = fromRectF.left + fromRectF.width() / 2 - viewRectF.width() / 2;
        final float startTranY = fromRectF.top + fromRectF.height() / 2 - viewRectF.height() / 2;

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                setMatrix(
                        startScale + value * (1 - startScale),
                        startTranX + value * (0 - startTranX),
                        startTranY + value * (0 - startTranY)
                );
            }
        });


        animator.start();
    }

    public float getScale() {
        return drawRectF.width() / viewRectF.width();
    }

    public float getTranslateX() {
        return drawRectF.left + drawRectF.width() / 2 - viewRectF.width() / 2;
    }

    public float getTranslateY() {
        return drawRectF.top + drawRectF.height() / 2 - viewRectF.width() / 2;
    }

    /**
     * @param scale 需要缩放到的倍数
     * @param tranX 需要移动到的x位置 (在缩放效果后, 相对于左上角的坐标)
     * @param tranY 需要移动到的y位置 (在缩放效果后, 相对于左上角的坐标)
     */
    private void setMatrix(float scale, float tranX, float tranY) {
        //Log.e("angcyo", "scale:" + scale + " tranX:" + tranX + " tranY:" + tranY);
        matrix.setScale(scale, scale);
        matrix.mapRect(drawRectF, viewRectF);
        matrix.postTranslate(viewRectF.width() / 2 - drawRectF.width() / 2, viewRectF.height() / 2 - drawRectF.height() / 2);
        matrix.postTranslate(tranX, tranY);
        matrix.mapRect(drawRectF, viewRectF);
        postInvalidateOnAnimation();

        if (onMatrixTouchListener != null) {
            onMatrixTouchListener.onMatrixChange(this, matrix, viewRectF, drawRectF);
        }
    }

    /**
     * 是否要开启事件检查, 关闭/开始功能
     */
    private boolean checkTouchEvent() {
        if (onMatrixTouchListener != null) {
            return onMatrixTouchListener.checkTouchEvent(this);
        }
        return true;
    }

    /**
     * 矩阵是否改变过
     */
    private boolean isMatrixChange() {
        matrix.mapRect(drawRectF, viewRectF);
        return !drawRectF.equals(viewRectF);
    }

    private boolean needTouchEvent() {
        boolean matrixChange = isMatrixChange();
        boolean checkTouchEvent = checkTouchEvent();
        boolean handle = checkTouchEvent || matrixChange;

        return handle;
    }

    public void setOnMatrixTouchListener(OnMatrixTouchListener onMatrixTouchListener) {
        this.onMatrixTouchListener = onMatrixTouchListener;
    }

    /**
     * 恢复默认状态
     */
    public void resetMatrix() {
        matrix.reset();
        postInvalidate();
    }

    public interface OnMatrixTouchListener {
        boolean checkTouchEvent(@NonNull MatrixLayout matrixLayout);

        void onMatrixChange(@NonNull MatrixLayout matrixLayout,
                            @NonNull Matrix matrix,
                            @NonNull RectF fromRect,
                            @NonNull RectF toRect);

        /**
         * @return 返回true, 拦截布局的默认处理方式
         */
        boolean onTouchEnd(@NonNull MatrixLayout matrixLayout,
                           @NonNull Matrix matrix,
                           @NonNull RectF fromRect,
                           @NonNull RectF toRect);
    }
}
