package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.huangchengxi.funnytrip.R;

public class SwitchOptionView extends FrameLayout {
    private TextView mainTV;
    private Switch switchButton;
    private OnCheckStateChangedListener onCheckStateChangedListener;

    public SwitchOptionView(@NonNull Context context) {
        this(context,null);
    }

    public SwitchOptionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwitchOptionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public SwitchOptionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }
    private void init(Context context,AttributeSet attrs){
        inflate(context, R.layout.view_switch_option,this);
        mainTV=findViewById(R.id.main);
        switchButton=findViewById(R.id.switch_button);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (onCheckStateChangedListener!=null){
                    onCheckStateChangedListener.onChanged(b);
                }
            }
        });
        TypedArray array=context.obtainStyledAttributes(attrs,R.styleable.SwitchOptionView);
        String main=array.getString(R.styleable.SwitchOptionView_main);
        boolean on=array.getBoolean(R.styleable.SwitchOptionView_on,false);
        mainTV.setText(main);
        switchButton.setChecked(on);
    }
    public void setSwitchButton(boolean on){
        switchButton.setChecked(on);
    }
    public interface OnCheckStateChangedListener{
        void onChanged(boolean on);
    }
    public void setOnCheckStateChangedListener(OnCheckStateChangedListener onCheckStateChangedListener) {
        this.onCheckStateChangedListener = onCheckStateChangedListener;
    }
}
