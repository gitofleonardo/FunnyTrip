package cn.huangchengxi.funnytrip.item;

import java.util.List;

public class RouteItem {
    private List<PositionItem> route;
    private String routeId;

    public RouteItem(List<PositionItem> list,String routeId){
        this.route=list;
        this.routeId=routeId;
    }

    public List<PositionItem> getRoute() {
        return route;
    }

    public String getRouteId() {
        return routeId;
    }
}
