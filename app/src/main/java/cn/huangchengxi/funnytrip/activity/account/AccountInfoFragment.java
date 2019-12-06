package cn.huangchengxi.funnytrip.activity.account;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.login.LoginException;

import cn.huangchengxi.funnytrip.R;
import cn.huangchengxi.funnytrip.activity.navigation.AccountInfoActivity;
import cn.huangchengxi.funnytrip.application.MainApplication;
import cn.huangchengxi.funnytrip.databean.PortraitUrlBean;
import cn.huangchengxi.funnytrip.databean.UserInformationBean;
import cn.huangchengxi.funnytrip.utils.HttpHelper;
import cn.huangchengxi.funnytrip.utils.StorageHelper;
import cn.huangchengxi.funnytrip.view.AddressPickerDialog;
import cn.huangchengxi.funnytrip.view.OptionDetailView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class AccountInfoFragment extends Fragment {
    private MyHandler myHandler=new MyHandler();
    private OnFragmentInteractionListener mListener;
    private OnDataChanged onDataChanged;

    private final int SELECT_PICTURE=0;
    private final int CROP_PICTURE=1;
    private final int SET_NAME=3;
    private final int SET_INTEREST=4;
    private final int CONNECTION_FAILED=5;
    private final int UPLOAD_PORTRAIT_SUCCESS=9;
    private final int UPLOAD_PORTRAIT_FAILED=10;

    private int newGender=0;
    private long newBirthday=0;
    private String newHomeland=null;
    private String newLocation=null;
    private String newCareer=null;
    private String newInterest=null;

    private boolean changed=false;

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

    private UserInformationBean bean;

    public AccountInfoFragment() {
        // Required empty public constructor
    }
    public static AccountInfoFragment newInstance(UserInformationBean bean) {
        AccountInfoFragment fragment = new AccountInfoFragment();
        fragment.setBean(bean);
        return fragment;
    }

    public void setBean(UserInformationBean bean) {
        this.bean = bean;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_account_info, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        portrait=view.findViewById(R.id.portrait);
        selectPor=view.findViewById(R.id.select_portrait);
        setName=view.findViewById(R.id.set_name);
        setUID=view.findViewById(R.id.set_id);
        setRegTime=view.findViewById(R.id.set_reg_time);
        setSign=view.findViewById(R.id.set_sign);
        setGender=view.findViewById(R.id.set_gender);
        setHomeland=view.findViewById(R.id.set_homeland);
        setLocation=view.findViewById(R.id.set_location);
        setCareer=view.findViewById(R.id.set_career);
        setInterest=view.findViewById(R.id.set_interest);
        setBirth=view.findViewById(R.id.set_birthday);

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
                startActivityForResult(new Intent(getContext(), SetAccountNameActivity.class),SET_NAME);
            }
        });
        setUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager=(ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("Label","UID:"+bean.getUid());
                manager.setPrimaryClip(data);
                Toast.makeText(getContext(), "UID已复制", Toast.LENGTH_SHORT).show();
            }
        });
        setRegTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager=(ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data=ClipData.newPlainText("Label","RegTime:"+bean.getRegisterTime());
                manager.setPrimaryClip(data);
                Toast.makeText(getContext(), "注册时间已复制", Toast.LENGTH_SHORT).show();
            }
        });
        setSign.setVisibility(View.GONE);
        setSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), SetSignActivity.class));
            }
        });
        setGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder al=new AlertDialog.Builder(getContext());
                String [] items={"男", "女", "保密"};
                al.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                newGender=1;
                                setGender.setSubText("男");
                                break;
                            case 1:
                                newGender=2;
                                setGender.setSubText("女");
                                break;
                            case 2:
                                setGender.setSubText("保密");
                                newGender=0;
                                break;
                        }
                        changed=true;
                        notifyDatasetChanged();
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

                DatePickerDialog dpd=new DatePickerDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog,new OnDateSet(),year,month,day);
                dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        changed=true;
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            newBirthday=sdf.parse(i+"-"+(++i1)+"-"+i2).getTime();
                            setBirth.setSubText(i+"-"+(++i1)+"-"+i2);
                            notifyDatasetChanged();
                        }catch (Exception e){
                        }
                    }
                });
                dpd.show();
            }
        });
        setHomeland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressPickerDialog dialog=new AddressPickerDialog(getContext());
                dialog.setOnSureClick(new AddressPickerDialog.OnSureClick() {
                    @Override
                    public void onClick(AddressPickerDialog dialog, String address, String provinceCode, String cityCode, String districtCode) {
                        changed=true;
                        newHomeland=address;
                        setHomeland.setSubText(newHomeland);
                        notifyDatasetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        setLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddressPickerDialog dialog=new AddressPickerDialog(getContext());
                dialog.setOnSureClick(new AddressPickerDialog.OnSureClick() {
                    @Override
                    public void onClick(AddressPickerDialog dialog, String address, String provinceCode, String cityCode, String districtCode) {
                        changed=true;
                        newLocation=address;
                        setLocation.setSubText(newLocation);
                        notifyDatasetChanged();
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
                AlertDialog.Builder al=new AlertDialog.Builder(getContext());
                al.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changed=true;
                        newCareer=items[which];
                        setCareer.setSubText(newCareer);
                        notifyDatasetChanged();
                    }
                });
                al.show();
            }
        });
        setInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), SetInterestActivity.class);
                intent.putExtra("interests",bean.getInterest());
                startActivityForResult(intent,SET_INTEREST);
            }
        });
        if (!bean.getPortraitUrl().equals("null")){
            Glide.with(getContext()).load(HttpHelper.SERVER_HOST+bean.getPortraitUrl()).into(portrait);
        }
        if (!bean.getNickname().equals("null")){
            setName.setSubText(bean.getNickname());
        }
        if (!bean.getUid().equals("null")){
            setUID.setSubText(bean.getUid());
        }
        setRegTime.setSubText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getRegisterTime())));
        if (bean.getGender()!=0){
            setGender.setSubText(bean.getGender()==1?"男":"女");
        }
        newGender=bean.getGender();
        if (bean.getBirthday()!=0){
            setBirth.setSubText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(bean.getBirthday())));
        }
        if (!bean.getHomeland().equals("null")){
            setHomeland.setSubText(bean.getHomeland());
        }
        if (!bean.getAddress().equals("null")){
            setLocation.setSubText(bean.getAddress());
        }
        if (!bean.getCareer().equals("null")){
            setCareer.setSubText(bean.getCareer());
        }
        if (!bean.getInterest().equals("null")){
            setInterest.setSubText("");
        }
    }
    private void notifyDatasetChanged(){
        if (onDataChanged!=null){
            onDataChanged.onChange(newGender,newBirthday,newHomeland,newLocation,newCareer,newInterest);
        }
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
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPLOAD_PORTRAIT_FAILED:
                    Toast.makeText(getContext(), "上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_PORTRAIT_SUCCESS:
                    Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    String url=(String)msg.obj;
                    Glide.with(getContext()).load(HttpHelper.SERVER_HOST+url).into(portrait);
                    break;
                case CONNECTION_FAILED:
                    Toast.makeText(getContext(), "网络连接失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case SELECT_PICTURE:
                if (resultCode==RESULT_OK){
                    startPhotoZoom(data.getData());
                }
                break;
            case CROP_PICTURE:
                if (resultCode==RESULT_OK){
                    HttpHelper.uploadPortrait(StorageHelper.getExternalDirectory() + "/tmp.png", getContext(), new HttpHelper.OnPortraitProcess() {
                        @Override
                        public void onReturnSuccess(PortraitUrlBean bean) {
                            Message m=myHandler.obtainMessage();
                            m.what=UPLOAD_PORTRAIT_SUCCESS;
                            m.obj=bean.getPortrait();
                            myHandler.sendMessage(m);
                        }
                        @Override
                        public void onReturnFailure() {
                            sendMessage(CONNECTION_FAILED);
                        }
                    });
                }
                break;
            case SET_INTEREST:
                if (data!=null){
                    newInterest=data.getStringExtra("interests");
                    changed=true;
                    Log.e("new interest",newInterest);
                    if (newInterest.equals("")){
                        newInterest=null;
                    }
                }
                break;
            case SET_NAME:
                if (data!=null){
                    setName.setSubText(data.getStringExtra("newName"));
                }
                break;
        }
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT,getUriForFile(getContext(),file));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent,CROP_PICTURE);
    }
    private static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        uri = Uri.fromFile(file);
        return uri;
    }
    private void sendMessage(int what){
        Message msg=myHandler.obtainMessage();
        msg.what=what;
        myHandler.sendMessage(msg);
    }
    public interface OnDataChanged{
        void onChange(int newGender,long newBirthday,String newHomeland,String newLocation,String newCareer,String newInterest);
    }
    public void setOnDataChanged(OnDataChanged onDataChanged) {
        this.onDataChanged = onDataChanged;
    }
}
