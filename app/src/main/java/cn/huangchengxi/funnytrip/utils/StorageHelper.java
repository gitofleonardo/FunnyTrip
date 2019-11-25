package cn.huangchengxi.funnytrip.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

public class StorageHelper {
    public static String getRealFilePath(final Context context, final Uri uri){
        if (uri==null){
            return null;
        }
        final String scheme=uri.getScheme();
        String data=null;
        if (scheme==null){
            data=uri.getPath();
        }else if (ContentResolver.SCHEME_FILE.equals(scheme)){
            data=uri.getPath();
        }else if (ContentResolver.SCHEME_CONTENT.equals(scheme)){
            Cursor cursor=context.getContentResolver().query(uri,new String[]{MediaStore.Images.ImageColumns.DATA},null,null,null);
            if (cursor!=null){
                if (cursor.moveToFirst()){
                    int index=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index>-1){
                        data=cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    public static String getExternalDirectory(){
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/FunnyTrip";
        File file=new File(path);
        if (!file.exists()){
            file.mkdir();
        }
        return path;
    }
}
