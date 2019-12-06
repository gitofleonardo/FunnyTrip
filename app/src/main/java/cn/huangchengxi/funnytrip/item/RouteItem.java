package cn.huangchengxi.funnytrip.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteItem implements Serializable {
    private ArrayList<PositionItem> route;
    private String routeId;
    private String name;
    private Long time;

    public RouteItem(ArrayList<PositionItem> list,String routeId,String name,Long time){
        this.route=list;
        this.routeId=routeId;
        this.name=name;
        this.time=time;
    }
    public long getTime() {
        return time;
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
