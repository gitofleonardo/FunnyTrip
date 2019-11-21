package cn.huangchengxi.funnytrip.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class HeadTitleBehavoir extends CoordinatorLayout.Behavior<TextView> {
    private float stableX=0;
    private float stableY=0;
    private Float oldX;
    private Float oldY;
    private float minPercent=0.7f;

    public HeadTitleBehavoir(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull TextView child, @NonNull View dependency) {
        return true;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull TextView child, @NonNull View dependency) {
        if (oldX==null || oldY==null){
            oldX=child.getX();
            oldY=child.getY();
        }
        float percent=1-dependency.getY()/dependency.getHeight();

        if (percent>1){
            percent=1;
        }
        if (percent<0){
            percent=0;
        }
        stableY=percent<0.3f?0.3f*oldY:percent*oldY;
        stableX=percent<0.3f?0.3f*oldX:percent*oldX;
        //child.setX(stableX);
        child.setY(stableY);
        child.setScaleX(percent<minPercent?minPercent:percent);
        child.setScaleY(percent<minPercent?minPercent:percent);

        return true;
    }
}
