package cn.huangchengxi.funnytrip.behavior;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class HomeToolBarBehavior extends CoordinatorLayout.Behavior<Toolbar> {
    public HomeToolBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull Toolbar child, @NonNull View dependency) {
        float percent=dependency.getY()/dependency.getHeight();
        percent*=1.25;
        if (percent>1){
            percent=1;
        }
        float alpha=percent*250;

        child.setBackgroundColor(Color.argb((int)alpha,3,169,244));
        return true;
    }
}
