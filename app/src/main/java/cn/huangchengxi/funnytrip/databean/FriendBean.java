package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.FriendItem;

public class FriendBean {
    private List<FriendItem> list;
    public FriendBean(List<FriendItem> list){
        this.list=list;
    }

    public List<FriendItem> getList() {
        return list;
    }
}
