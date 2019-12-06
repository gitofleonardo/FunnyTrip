package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.TipsItem;

public class TipsResultBean {
    private List<TipsItem> list;
    public TipsResultBean(List<TipsItem> list){
        this.list=list;
    }

    public List<TipsItem> getList() {
        return list;
    }
}
