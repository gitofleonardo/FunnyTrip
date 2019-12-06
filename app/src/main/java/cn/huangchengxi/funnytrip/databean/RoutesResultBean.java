package cn.huangchengxi.funnytrip.databean;

import java.util.List;

import cn.huangchengxi.funnytrip.item.RouteItem;

public class RoutesResultBean {
    private List<RouteItem> list;
    public RoutesResultBean(List<RouteItem> list){
        this.list=list;
    }

    public List<RouteItem> getList() {
        return list;
    }
}
