package cn.huangchengxi.funnytrip.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class WeatherBehavior extends CoordinatorLayout.Behavior<FrameLayout> {
    private float minPercent=0.7f;
    private float stableY=0;
    private Float oldY;

    public WeatherBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull FrameLayout child, @NonNull View dependency) {
        if (oldY==null){
            oldY=child.getY();
        }
        float percent=1-dependency.getY()/dependency.getHeight();
        if (percent<0.1){
            percent=0.1f;
        }
        stableY=percent*oldY;

        child.setY(stableY);
        child.setScaleX(percent<minPercent?minPercent:percent);
        child.setScaleY(percent<minPercent?minPercent:percent);

        return true;
    }
}
