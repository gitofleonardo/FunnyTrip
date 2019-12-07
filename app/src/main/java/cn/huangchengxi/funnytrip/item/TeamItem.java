package cn.huangchengxi.funnytrip.item;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TeamItem {
    private String teamId;
    private long time;
    private String teamName;

    public TeamItem(String teamId,String teamName,long time){
        this.time=time;
        this.teamId=teamId;
        this.teamName=teamName;
    }

    public String getFormattedDate(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time));
    }
    public String getTeamId() {
        return teamId;
    }


    public String getTeamName() {
        return teamName;
    }
}