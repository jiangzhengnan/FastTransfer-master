package com.guohui.fasttransfer.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {

    private float xDistance,xLast;
// private boolean noScroll = false;

    public NoScrollViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

// public boolean isNoScroll() {
//  return noScroll;
// }
//
// public void setNoScroll(boolean noScroll) {
//  this.noScroll = noScroll;
// }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

// @Override
// public boolean onTouchEvent(MotionEvent arg0) {
//  /* return false;//super.onTouchEvent(arg0); */
//  if (noScroll)
//   return false;
//  else
//   return super.onTouchEvent(arg0);
// }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance=0f;
                xLast = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                if(xLast-curX < 0 && getCurrentItem() != 0){
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}