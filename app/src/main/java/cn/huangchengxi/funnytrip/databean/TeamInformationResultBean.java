package cn.huangchengxi.funnytrip.databean;

import java.util.ArrayList;

import cn.huangchengxi.funnytrip.item.TeamPartnerItem;

public class TeamInformationResultBean {
    private String teamName;
    private String teamCreatorUID;
    private ArrayList<TeamPartnerItem> list;
    public TeamInformationResultBean(String teamName,String teamCreatorUID,ArrayList<TeamPartnerItem> list){
        this.list=list;
        this.teamCreatorUID=teamCreatorUID;
        this.teamName=teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public ArrayList<TeamPartnerItem> getList() {
        return list;
    }

    public String getTeamCreatorUID() {
        return teamCreatorUID;
    }
}
