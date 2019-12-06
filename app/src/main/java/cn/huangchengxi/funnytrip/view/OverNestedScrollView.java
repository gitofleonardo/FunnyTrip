package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

public class OverNestedScrollView extends NestedScrollView {

    private View mInnerView;// 孩子View

    private float mDownY;// 点击时y坐标

    private Rect mRect = new Rect();
    private int offset;

    private boolean isCount = false;// 是否开始计算

    private int mWidth;
    private int mHeight;

    public OverNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public OverNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取的就是 scrollview 的第一个子 View
        if (getChildCount() > 0) {
            mInnerView = getChildAt(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        MarginLayoutParams lp = (MarginLayoutParams) mInnerView.getLayoutParams();
        //减去 margin 的值
        offset = mInnerView.getMeasuredHeight() - lp.topMargin - lp.bottomMargin - mHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mInnerView != null) {
            commOnTouchEvent(e);
        }
        return super.onTouchEvent(e);
    }

    public void commOnTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY=e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float preY = mDownY;// 按下时的y坐标
                float nowY = e.getY();// 时时y坐标
                int deltaY = (int) (preY - nowY);// 滑动距离
                //排除出第一次移动计算无法得知y坐标
                if (!isCount) {
                    deltaY = 0;
                }
                mDownY = nowY;
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove()) {
                    if (mRect.isEmpty()) {
                        // 保存正常的布局位置
                        mRect.set(mInnerView.getLeft(), mInnerView.getTop(),
                                mInnerView.getRight(), mInnerView.getBottom());
                    }
                    // 移动布局
                    mInnerView.layout(mInnerView.getLeft(), mInnerView.getTop() - deltaY / 2,
                            mInnerView.getRight(), mInnerView.getBottom() - deltaY / 2);
                }
                isCount = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    translateAnimator();
                    isCount = false;
                }
                break;
        }
    }
    public void translateAnimator() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, mInnerView.getTop(), mRect.top);
        animation.setDuration(200);
        animation.setInterpolator(new FastOutSlowInInterpolator());
        mInnerView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mInnerView.startAnimation(animation);
        // 设置回到正常的布局位置
        mRect.setEmpty();
    }
    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !mRect.isEmpty();
    }

    // 判断是否处于顶部或者底部
    public boolean isNeedMove() {
        // 0是顶部，offset是底部
        if (getScrollY() == 0 || getScrollY() >= offset) {
            return true;
        }
        return false;
    }

}

