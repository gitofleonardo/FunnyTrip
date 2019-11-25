package cn.huangchengxi.funnytrip.activity.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.account.SetAccountNameActivity;
import cn.huangchengxi.funnytrip.activity.account.SetInterestActivity;
import cn.huangchengxi.funnytrip.activity.account.SetSignActivity;
import cn.huangchengxi.funnytrip.utils.PermissionHelper;
import cn.huangchengxi.funnytrip.utils.StorageHelper;
import cn.huangchengxi.funnytrip.view.AddressPickerDialog;
import cn.huangchengxi.funnytrip.view.OptionDetailView;

public class AccountInfoActivity extends AppCompatActivity {
    private final int SELECT_PICTURE=0;
    private final int CROP_PICTURE=1;
    private final int REQUEST_PERMISSION=2;

    private Toolbar toolbar;
    private ImageView back;
    private ImageView portrait;
    private FrameLayout selectPor;
    private OptionDetailView setName;
    private OptionDetailView setUID;
    private OptionDetailView setRegTime;
    private OptionDetailView setSign;
    private OptionDetailView setGender;
    private OptionDetailView setBirth;
    private OptionDetailView setHomeland;
    private OptionDetailView setLocation;
    private OptionDetailView setCareer;
    private OptionDetailView setInterest;

    private Bitmap portraitBitmap;
    private MyHandler myHandler=new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        init();
    }
    private void init(){
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        portrait=findViewById(R.id.portrait);
        selectPor=findViewById(R.id.select_portrait);
        setName=findViewById(R.id.set_name);
        setUID=findViewById(R.id.set_id);
        setRegTime=findViewById(R.id.set_reg_time);
        setSign=findViewById(R.id.set_sign);
        setGender=findViewById(R.id.set_gender);
        setHomeland=findViewById(R.id.set_homeland);
        setLocation=findViewById(R.id.set_location);
        setCareer=findViewById(R.id.set_career);
        setInterest=findViewById(R.id.set_interest);
        setBirth=findViewById(R.id.set_birthday);

        selectPor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK,null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,SELECT_PICTURE);
            }
        });
        setName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountInfoActivity.this, SetAccountNameActivity.class));
            }
        });
        setUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("Label","UID:null");
                manager.setPrimaryClip(data);
                Toast.makeText(AccountInfoActivity.this, "UID已复制", Toast.LENGTH_SHORT).show();
            }
        });
        setRegTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("Label","RegTime:null");
                manager.setPrimaryClip(data);
                Toast.makeText(AccountInfoActivity.this, "注册时间已复制", Toast.LENGTH_SHORT).show();
            }
        });
        setSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountInfoActivity.this, SetSignActivity.class));
            }
        });
        setGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder al=new AlertDialog.Builder(AccountInfoActivity.this);
                String [] items={"男", "女", "保密"};
                al.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                        }
                    }
                });
                al.setCancelable(true);
                al.show();
            }
        });
        setBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date=new Date();
                SimpleDateFormat sdfy=new SimpleDateFormat("yyyy");
                SimpleDateFormat sdfM=new SimpleDateFormat("MM");
                SimpleDateFormat sdfd=new SimpleDateFormat("dd");
                int year=Integer.parseInt(sdfy.format(date));
                int month=Integer.parseInt(sdfM.format(date));
                int day=Integer.parseInt(sdfd.format(date));

                DatePickerDialog dpd=new DatePickerDialog(AccountInfoActivity.this, R.style.Theme_AppCompat_Light_Dialog,new OnDateSet(),year,month,day);
                dpd.show();
            }
        });
        setHomeland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressPickerDialog dialog=new AddressPickerDialog(AccountInfoActivity.this);
                dialog.setOnSureClick(new AddressPickerDialog.OnSureClick() {
                    @Override
                    public void onClick(AddressPickerDialog dialog, String address, String provinceCode, String cityCode, String districtCode) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressPickerDialog dialog=new AddressPickerDialog(AccountInfoActivity.this);
                dialog.setOnSureClick(new AddressPickerDialog.OnSureClick() {
                    @Override
                    public void onClick(AddressPickerDialog dialog, String address, String provinceCode, String cityCode, String districtCode) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        setCareer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items={"计算机/互联网/通信","生产/工艺/制造 ","医疗/护理/制药","金融/银行/投资/保险","商业/服务业/个体经营","文化/广告/传媒","娱乐/艺术/表演","律师/法务","教育/培训","公务员/行政/事业单位","模特","空姐","学生","其他职业"};
                AlertDialog.Builder al=new AlertDialog.Builder(AccountInfoActivity.this);
                al.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                al.show();
            }
        });
        setInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountInfoActivity.this, SetInterestActivity.class));
            }
        });
    }
    private void startPhotoZoom(Uri uri){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",300);
        intent.putExtra("outputY",300);
        intent.putExtra("scale",true);
        intent.putExtra("return-data",false);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File file=new File(StorageHelper.getExternalDirectory()+"/tmp.png");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,getUriForFile(this,file));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent,CROP_PICTURE);
    }

    private class OnDateSet implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date=simpleDateFormat.parse(year+"-"+(month+1)+"-"+dayOfMonth);
            }catch (Exception e){
                Log.e("date error",e.toString());
            }
        }
    }
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        //暂时保留
        /*
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), "cn.huangchengxi.funnytrip", file);
        } else {

        }
         */
        uri = Uri.fromFile(file);
        return uri;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_PICTURE:
                if (resultCode==RESULT_OK){
                    startPhotoZoom(data.getData());
                }
                break;
            case CROP_PICTURE:
                if (resultCode==RESULT_OK){
                    portraitBitmap=BitmapFactory.decodeFile(StorageHelper.getExternalDirectory()+"/tmp.png");
                    portrait.setImageBitmap(portraitBitmap);
                }
                break;
        }
    }
    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    break;
            }
        }
    }
}
