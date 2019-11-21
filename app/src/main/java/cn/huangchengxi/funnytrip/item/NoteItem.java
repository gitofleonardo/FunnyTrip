package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteItem {
    private long time;
    private String content;
    private String noteId;
    public NoteItem(long time,String content,String noteId){
        this.content=content;
        this.noteId=noteId;
        this.time=time;
    }

    public String getContent() {
        return content;
    }

    public long getTime() {
        return time;
    }

    public String getNoteId() {
        return noteId;
    }
    public String getFormattedTime(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(time));
    }
}
