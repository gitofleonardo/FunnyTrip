package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.huangchengxi.funnytrip.R;

public class OptionDetailView extends FrameLayout {
    private TextView mainText;
    private TextView subText;

    public OptionDetailView(@NonNull Context context) {
        this(context,null);
    }

    public OptionDetailView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public OptionDetailView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public OptionDetailView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attrs){
        inflate(context, R.layout.view_detail_option,this);
        mainText=findViewById(R.id.main);
        subText=findViewById(R.id.sub);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.OptionDetailView);
        String main=array.getString(R.styleable.OptionDetailView_main);
        String sub=array.getString(R.styleable.OptionDetailView_sub);
        if (main!=null){
            mainText.setText(main);
        }
        if (sub!=null){
            subText.setText(sub);
        }
        array.recycle();
    }
    public void setSubText(String text){
        this.subText.setText(text);
    }
}
