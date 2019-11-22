package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.huangchengxi.funnytrip.R;

public class AboutOption extends FrameLayout {
    private TextView mainTV;
    private TextView subTV;

    public AboutOption(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public AboutOption(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public AboutOption(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AboutOption(@NonNull Context context) {
        this(context,null);
    }
    private void init(Context context,AttributeSet attrs){
        inflate(context, R.layout.view_about_item,this);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.AboutOption);
        String main=array.getString(R.styleable.AboutOption_main);
        String sub=array.getString(R.styleable.AboutOption_sub);
        mainTV=findViewById(R.id.main);
        subTV=findViewById(R.id.sub);
        mainTV.setText(main);
        subTV.setText(sub);
        array.recycle();
    }
}
