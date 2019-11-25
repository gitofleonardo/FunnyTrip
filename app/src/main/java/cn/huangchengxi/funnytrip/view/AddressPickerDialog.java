package cn.huangchengxi.funnytrip.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import cn.huangchengxi.funnytrip.R;

public class AddressPickerDialog extends AlertDialog {
    private OnSureClick onSureClick;
    private AddressPickerView addressPickerView;

    public AddressPickerDialog(@NonNull Context context) {
        super(context);
    }

    protected AddressPickerDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected AddressPickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_address_picker_dialog);
        init(getContext());
    }

    private void init(Context context){
        addressPickerView=findViewById(R.id.address_picker);
        addressPickerView.setOnAddressPickerSure(new AddressPickerView.OnAddressPickerSureListener() {
            @Override
            public void onSureClick(String address, String provinceCode, String cityCode, String districtCode) {
                if (onSureClick!=null){
                    onSureClick.onClick(AddressPickerDialog.this,address,provinceCode,cityCode,districtCode);
                }
            }
        });
        WindowManager windowManager=getWindow().getWindowManager();
        WindowManager.LayoutParams params=getWindow().getAttributes();
        Display display=windowManager.getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);
        params.width=point.x;
        getWindow().setAttributes(params);
        getWindow().setGravity(Gravity.CENTER);

        setCancelable(true);
    }
    public interface OnSureClick{
        void onClick(AddressPickerDialog dialog,String address,String provinceCode,String cityCode,String districtCode);
    }

    public void setOnSureClick(OnSureClick onSureClick) {
        this.onSureClick = onSureClick;
    }
}
