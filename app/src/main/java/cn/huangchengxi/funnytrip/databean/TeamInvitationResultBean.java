package cn.huangchengxi.funnytrip.databean;

import java.util.ArrayList;

import cn.huangchengxi.funnytrip.item.TeamInvitationItem;

public class TeamInvitationResultBean {
    private ArrayList<TeamInvitationItem> list;
    public TeamInvitationResultBean(ArrayList<TeamInvitationItem> list){
        this.list=list;
    }
    public ArrayList<TeamInvitationItem> getList() {
        return list;
    }
}
