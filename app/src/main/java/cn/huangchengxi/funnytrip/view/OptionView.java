package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.huangchengxi.funnytrip.R;

public class OptionView extends FrameLayout {
    private TextView des;

    public OptionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public OptionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public OptionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OptionView(@NonNull Context context) {
        this(context,null);
    }
    private void init(Context context,AttributeSet attrs){
        inflate(context, R.layout.view_option_item,this);
        des=findViewById(R.id.name);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.OptionView);
        String text=array.getString(R.styleable.OptionView_text);
        if (text!=null){
            des.setText(text);
        }
    }
}
