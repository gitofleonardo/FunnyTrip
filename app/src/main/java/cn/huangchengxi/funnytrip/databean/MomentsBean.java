package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.MomentItem;

public class MomentsBean {
    private List<MomentItem> list;
    public MomentsBean(List<MomentItem> list){
        this.list=list;
    }

    public List<MomentItem> getList() {
        return list;
    }
}
