package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.ClockItem;

public class ClocksResultBean {
    private List<ClockItem> list;
    public ClocksResultBean(List<ClockItem> list){
        this.list=list;
    }

    public List<ClockItem> getList() {
        return list;
    }
}
