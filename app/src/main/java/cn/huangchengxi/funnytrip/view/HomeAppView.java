package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.huangchengxi.funnytrip.R;

public class HomeAppView extends FrameLayout {
    private ImageView imageView;
    private TextView textView;

    public HomeAppView(Context context) {
        this(context,null);
    }
    public HomeAppView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }
    public HomeAppView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }
    public HomeAppView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }
    private void init(Context context,AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.view_home_app,this);
        imageView=findViewById(R.id.home_app_icon);
        textView=findViewById(R.id.home_app_name);
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.HomeAppView);
        String name=array.getString(R.styleable.HomeAppView_name);
        int Rid=array.getResourceId(R.styleable.HomeAppView_icon,R.drawable.ic_launcher_background);
        array.recycle();
        if (name!=null){
            textView.setText(name);
        }else{
            textView.setText(getResources().getString(R.string.app));
        }
        imageView.setImageResource(Rid);
    }
}
