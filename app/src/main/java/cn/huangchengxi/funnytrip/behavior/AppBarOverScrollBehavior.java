package cn.huangchengxi.funnytrip.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class AppBarOverScrollBehavior extends AppBarLayout.Behavior {
    private static final String TAG="overScroll";
    private View targetView;
    private int parentHeight;
    private int targetHeight;

    private final float MAX_TARGET_HEIGHT=500;
    private float totalDy;
    private float lastScale;
    private int lastBottom;

    private boolean isAnimated=true;
    private ValueAnimator animator;

    private static final int TYPE_FLYING=1;
    private boolean isFlying;
    private boolean shouldBlockNestedScroll;

    public AppBarOverScrollBehavior() {
        super();
    }

    public AppBarOverScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout abl, int layoutDirection) {
        boolean handled=super.onLayoutChild(parent, abl, layoutDirection);
        if (targetView==null){
            targetView=parent.findViewWithTag(TAG);
            if (targetView!=null){
                abl.setClipChildren(false);
                parentHeight=abl.getHeight();
                targetHeight=targetView.getHeight();
            }
        }
        return handled;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull MotionEvent ev) {
        shouldBlockNestedScroll=false;
        if (isFlying){
            shouldBlockNestedScroll=true;
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    //主要是让向下越界滑动时AppBarLayout变高 背景图片放大
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        if (type==TYPE_FLYING){
            isFlying=true;
        }
        if (targetView!=null && ((dy<0 && child.getBottom()>=parentHeight) || (dy>0 && child.getBottom() > parentHeight))){
            //1. targetView is not null
            //2. is head down scrolling
            //3. app bar layout is totally expanded
            totalDy+=-dy;
            totalDy=Math.min(totalDy,MAX_TARGET_HEIGHT);
            lastScale=Math.max(1f,1f+totalDy/MAX_TARGET_HEIGHT);
            targetView.setScaleX(lastScale);
            targetView.setScaleY(lastScale);
            lastBottom=parentHeight+(int)(targetHeight/2*(lastScale-1));
            //Log.e("bottom",String.valueOf(lastBottom));
            child.setBottom(lastBottom);
            target.setScrollY(0);
        }else{
            if (!shouldBlockNestedScroll){
                super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
            }
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
        if (!shouldBlockNestedScroll){
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View directTargetChild, View target, int nestedScrollAxes, int type) {
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, @NonNull View target, float velocityX, float velocityY) {
        //return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
        if (child.getBottom()==child.getHeight()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull final AppBarLayout abl, View target, int type) {
        isFlying=false;
        shouldBlockNestedScroll=false;
        if (totalDy>0){
            totalDy=0;
            if (isAnimated){
                final float scale=lastScale;
                if (animator!=null){
                    animator.cancel();
                }
                animator=ValueAnimator.ofFloat(lastScale,1f).setDuration(200);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float val=(1-scale)*valueAnimator.getAnimatedFraction()+scale;
                        //Log.e("currentBottom",String.valueOf(abl.getBottom()));
                        targetView.setScaleY(val);
                        targetView.setScaleX(val);
                        abl.setBottom((int) (lastBottom-(lastBottom-parentHeight)*valueAnimator.getAnimatedFraction()));
                    }
                });
                animator.start();
            }else{
                targetView.setScaleX(1f);
                targetView.setScaleY(1f);
                abl.setBottom(parentHeight);
            }
        }
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);
    }
}
