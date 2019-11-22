package cn.huangchengxi.funnytrip.item;

import java.util.List;

public class RouteItem {
    private List<PositionItem> route;
    private String routeId;
    private String name;

    public RouteItem(List<PositionItem> list,String routeId,String name){
        this.route=list;
        this.routeId=routeId;
        this.name=name;
    }

    public List<PositionItem> getRoute() {
        return route;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getName() {
        return name;
    }
}
