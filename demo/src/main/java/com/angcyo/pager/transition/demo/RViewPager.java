package com.angcyo.pager.transition.demo;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by angcyo on 2017-01-14.
 */

public class RViewPager extends ViewPager {

    private static final String TAG = "angcyo";
    private int mOrientation = LinearLayout.HORIZONTAL;
    private GestureDetectorCompat mGestureDetectorCompat;

    private OnPagerEndListener mOnPagerEndListener;

    private int heightMeasureMode = MeasureSpec.EXACTLY;

    public RViewPager(Context context) {
        this(context, null);
    }

    public RViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (mOnPagerEndListener != null &&
                        getAdapter() != null &&
                        getCurrentItem() == getAdapter().getCount() - 1) {
                    if (mOrientation == LinearLayout.VERTICAL) {
                        if (velocityY < -1000) {
                            mOnPagerEndListener.onPagerEnd();
                        }
                    } else {
                        if (velocityX < -1000) {
                            mOnPagerEndListener.onPagerEnd();
                        }
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (heightMeasureMode != MeasureSpec.EXACTLY) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            requestLayout();
                        }
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public void setOnPagerEndListener(OnPagerEndListener onPagerEndListener) {
        mOnPagerEndListener = onPagerEndListener;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    public void resetItem(int position) {
        PagerAdapter adapter = getAdapter();
        if (adapter != null) {
            adapter.destroyItem(this, position, getChildAt(position));
            adapter.instantiateItem(this, position);
        }
//        if (adapter instanceof RPagerAdapter) {
//            WeakReference<View> viewWeakReference = ((RPagerAdapter) adapter).mViewCache.get(position);
//            View view = null;
//            if (viewWeakReference != null) {
//                view = viewWeakReference.get();
//            }
//            if (view != null) {
//                ((RPagerAdapter) adapter).initItemView(view, position);
//            }
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        heightMeasureMode = heightMode;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (heightMode == MeasureSpec.EXACTLY) {
        } else {
            //支持高度的wrap_content
            if (getChildCount() > getCurrentItem()) {
                View childAt = getChildAt(getCurrentItem());
                measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(widthSize, childAt.getMeasuredHeight() + getPaddingLeft() + getPaddingRight());
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            if (mOrientation == LinearLayout.VERTICAL) {
                return mGestureDetectorCompat.onTouchEvent(ev)
                        || super.onTouchEvent(swapTouchEvent(ev));
            }
            return mGestureDetectorCompat.onTouchEvent(ev)
                    || super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            if (mOrientation == LinearLayout.VERTICAL) {
                return super.onInterceptTouchEvent(swapTouchEvent(ev));
            }
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private MotionEvent swapTouchEvent(MotionEvent event) {
        float width = getWidth();
        float height = getHeight();

        float swappedX = (event.getY() / height) * width;
        float swappedY = (event.getX() / width) * height;

        event.setLocation(swappedX, swappedY);

        return event;
    }


    public interface OnPagerEndListener {
        /**
         * 最后一一页快速滚动
         */
        void onPagerEnd();
    }
}
