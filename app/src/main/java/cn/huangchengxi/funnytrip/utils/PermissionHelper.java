package cn.huangchengxi.funnytrip.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;

public class PermissionHelper {
    private PermissionHelper(){}
    public static String[] neededPermissions={
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_WIFI_STATE};

    public static void requestNeededPermissions(Context context,int requestCode){
        for (String permission:neededPermissions){
            if (context.checkSelfPermission(permission)!= PackageManager.PERMISSION_GRANTED){
                ((AppCompatActivity)context).requestPermissions(neededPermissions,requestCode);
            }
        }
    }
    public static boolean checkNeededPermissions(Context context){
        for (String permission:neededPermissions){
            if (context.checkSelfPermission(permission)!=PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
}
