package cn.huangchengxi.funnytrip.utils.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocalUsersUpdate {
    public static void InsertOrUpdate(Context context,String uid, String name, String portraitUrl){
        SqliteHelper helper=new SqliteHelper(context,"local_users",null,1);
        SQLiteDatabase db=helper.getWritableDatabase();
        Cursor cursor=db.query("local_users",null,"uid=?",new String[]{uid},null,null,null);
        if (cursor.moveToFirst()){
            db.execSQL("update local_users set nickname=\""+name+"\",portrait_url=\""+portraitUrl+"\" where uid=\""+uid+"\"");
        }else{
            db.execSQL("insert into local_users values(\""+uid+"\",\""+name+"\",\""+portraitUrl+"\")");
        }
    }
}
