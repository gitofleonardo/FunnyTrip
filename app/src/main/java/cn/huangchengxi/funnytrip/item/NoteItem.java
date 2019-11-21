package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteItem {
    private long time;
    private String content;

    public NoteItem(long time,String content){
        this.content=content;
        this.time=time;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }
}
