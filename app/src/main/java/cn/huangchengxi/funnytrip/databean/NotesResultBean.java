package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.NoteItem;

public class NotesResultBean {
    private List<NoteItem> list;
    public NotesResultBean(List<NoteItem> list){
        this.list=list;
    }

    public List<NoteItem> getList() {
        return list;
    }
}
