package cn.huangchengxi.funnytrip.utils.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteHelper extends SQLiteOpenHelper {
    public SqliteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String weatherTable="create table weather(id varchar(50) unique)";
        String noteTable="create table notes(note_time long unique,content varchar(200))";
        String clockTable="create table clocks(clock_time long unique,location varchar(200),latitude double,longitude double)";
        String routeTable="create table routes(name varchar(20),route_time long)";
        String positionTable="create table positions(name varchar(100),latitude double,longitude double,route long,pos_index int)";
        String tipsUrl="create table tips(id long,url text,title text)";
        String settingTable="create table settings(max_note int,max_clock int)";
        db.execSQL(settingTable);
        db.execSQL(tipsUrl);
        db.execSQL(positionTable);
        db.execSQL(routeTable);
        db.execSQL(clockTable);
        db.execSQL(weatherTable);
        db.execSQL(noteTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
