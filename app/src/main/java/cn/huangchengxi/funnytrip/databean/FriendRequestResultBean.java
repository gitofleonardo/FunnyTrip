package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.FriendRequestItem;

public class FriendRequestResultBean {
    private List<FriendRequestItem> list;
    public FriendRequestResultBean(List<FriendRequestItem> list){
        this.list=list;
    }

    public List<FriendRequestItem> getList() {
        return list;
    }
}
