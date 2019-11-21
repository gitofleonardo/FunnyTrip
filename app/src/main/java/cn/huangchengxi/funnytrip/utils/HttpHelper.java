package cn.huangchengxi.funnytrip.utils;


import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpHelper {
    private HttpHelper(){}

    public static void sendOKHttpRequest(String address,okhttp3.Callback callback){
        Log.e("okhttp",address);
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
