package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.FriendSearchResultItem;

public class SearchResultBean {
    private List<FriendSearchResultItem> list;
    public SearchResultBean(List<FriendSearchResultItem> list){
        this.list=list;
    }
    public List<FriendSearchResultItem> getList() {
        return list;
    }
}
