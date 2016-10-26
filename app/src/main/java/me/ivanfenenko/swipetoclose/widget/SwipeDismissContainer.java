package me.ivanfenenko.swipetoclose.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;

import timber.log.Timber;

/**
 * Created by klickrent on 26/10/2016.
 */

public class SwipeDismissContainer extends FrameLayout {

    private View swipeView;
    private View backgroundView;
    private ArrayList<View> fadeViews = new ArrayList<>();
    private ScrollListener scrollListener;

    /* Positions of the last motion event */
    private float mLastTouchX, mLastTouchY;
    /* Drag threshold */
    private int mTouchSlop;
    /* Drag Lock */
    private boolean mDragging = false;
    private float mTouchImageViewDefaultY;
    private boolean isClosing;
    private int mTouchImageViewDefaultHeight;
    private boolean isDraggingDown;
    private boolean isDraggingUp;

    private GestureDetector gestureDetector;

    public SwipeDismissContainer(Context context) {
        super(context);

        init(context);
    }

    public SwipeDismissContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public SwipeDismissContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public void setFadeViews(@NonNull View ... views) {
        fadeViews = new ArrayList<>(Arrays.asList(views));
    }

    public void setFadeViews(@NonNull ArrayList views) {
        fadeViews = views;
    }

    public void setBackgroundView(View backgroundView) {
        this.backgroundView = backgroundView;
    }

    public void setSwipeView(View swipeView){
        this.swipeView = swipeView;
    }

    private Activity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void init(Context context){
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        gestureDetector = new GestureDetector(context, new GestureTap());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (swipeView == null) swipeView = getChildAt(0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getPointerCount()==1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchImageViewDefaultY = swipeView.getY();
                    mTouchImageViewDefaultHeight = swipeView.getHeight();
                    //Save the initial touch point
                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float x = event.getX();
                    final float y = event.getY();
                    final int yDiff = (int) Math.abs(y - mLastTouchY);
                    final int xDiff = (int) Math.abs(x - mLastTouchX);
                    //Verify that either difference is enough to be a drag
                    if (yDiff > mTouchSlop || xDiff > mTouchSlop && swipeView.getHeight()>0) {
                        mDragging = true;
                        //Start capturing events ourselves
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mDragging = false;
                    break;
            }
        }

        gestureDetector.onTouchEvent(event);

        return super.onInterceptTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount()==1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // We've already stored the initial point,
                    // but if we got here a child view didn't capture
                    // the event, so we need to.
                    return true;
                case MotionEvent.ACTION_MOVE:
                    final float x = event.getX();
                    final float y = event.getY();
                    float deltaY = mLastTouchY - y;
                    float deltaX = mLastTouchX - x;
                    //Check for slop on direct events
                    if (!mDragging && (Math.abs(deltaY) > mTouchSlop || Math.abs(deltaX) > mTouchSlop) && swipeView.getHeight()>0) {
                        mDragging = true;
                    }
                    if (mDragging && !isClosing){
                        if (deltaY<0){
                            isDraggingDown = true;
                            isDraggingUp = false;

                            float alpha = Math.abs(getY() - swipeView.getY()) / (float) (mTouchImageViewDefaultHeight / 2) * (float) 100;
                            alpha = Math.min(100, alpha);
                            Timber.d("Drag aplha " + alpha);
                            if (backgroundView != null) backgroundView.setBackgroundColor(Color.argb(255-Math.round(alpha/100*255), 0, 0, 0));

                            for (View v : fadeViews) {
                                v.setAlpha((100 - alpha) / 100f);
                            }

                        } else {
                            isDraggingUp = true;
                            isDraggingDown = false;

                            float alpha = Math.abs(getY() - swipeView.getY()) / (float) (mTouchImageViewDefaultHeight / 2) * (float) 100;
                            alpha = Math.min(100, alpha);
                            Timber.d("Drag aplha " + alpha);
                            if (backgroundView != null) backgroundView.setBackgroundColor(Color.argb(255-Math.round(alpha/100*255), 0, 0, 0));

                            for (View v : fadeViews) {
                                v.setAlpha((100 - alpha) / 100f);
                            }
                        }

                        if (scrollListener!=null) scrollListener.onScroll(event.getX(), event.getY(), deltaX, deltaY);
                        swipeView.setY(swipeView.getY()-deltaY);
                        //Update the last touch event
                        mLastTouchX = x;
                        mLastTouchY = y;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mDragging = false;
                    break;
                case MotionEvent.ACTION_UP:
                    boolean back = false;
                    if (!isClosing){
                        if(isDraggingUp){
                            if (mTouchImageViewDefaultY - swipeView.getY() > mTouchImageViewDefaultHeight / 4){
                                getActivity().onBackPressed();
                                back = true;
                            } else{
                                swipeView.animate().y(0).setDuration(300).start();
                                swipeView.setY(0);
                                // We are not in scrolling up mode anymore
                                isDraggingUp = false;
                            }
                        }

                        // If user was doing a scroll down
                        if(isDraggingDown){
                            if (Math.abs(mTouchImageViewDefaultY - swipeView.getY()) > mTouchImageViewDefaultHeight / 4) {
                                getActivity().onBackPressed();
                                back = true;
                            } else {
                                // Reset baselayout position
                                swipeView.animate().y(0).setDuration(300).start();
                                swipeView.setY(0);
                                isDraggingDown = false;
                            }
                        }

                        if (!back) {
                            if (backgroundView != null) backgroundView.setBackgroundColor(Color.rgb(0, 0, 0));
                            for (View v : fadeViews) {
                                AlphaAnimation alphaAnimation = new AlphaAnimation(v.getAlpha(), 1f);
                                alphaAnimation.setDuration(300);
                                alphaAnimation.setInterpolator(new AccelerateInterpolator());
                                v.startAnimation(alphaAnimation);
                            }
                        }
                    }

                    mDragging = false;
                    break;
            }
        }

        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    class GestureTap extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (scrollListener!=null) scrollListener.onSingleTap();
            return false;
        }

    }

    public interface ScrollListener{
        void onScroll(float dx, float dy, float deltaX, float deltaY);
        void onCloseUp();
        void onCloseDown();
        void onSingleTap();
    }
}