package cn.huangchengxi.funnytrip.databean;

import java.util.ArrayList;

import cn.huangchengxi.funnytrip.item.TeamItem;

public class TeamResultBean {
    private ArrayList<TeamItem> list;
    public TeamResultBean(ArrayList<TeamItem> list){
        this.list=list;
    }

    public ArrayList<TeamItem> getList() {
        return list;
    }
}
