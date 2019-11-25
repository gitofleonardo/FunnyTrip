package cn.huangchengxi.funnytrip.item;

public class TeamItem {
    private String teamId;
    private boolean hasMsg;
    private String latestMsg;
    private String teamName;

    public TeamItem(String teamId,String teamName,boolean hasMsg,String latestMsg){
        this.hasMsg=hasMsg;
        this.latestMsg=latestMsg;
        this.teamId=teamId;
        this.teamName=teamName;
    }

    public String getLatestMsg() {
        return latestMsg;
    }

    public String getTeamId() {
        return teamId;
    }

    public boolean isHasMsg() {
        return hasMsg;
    }

    public String getTeamName() {
        return teamName;
    }
}