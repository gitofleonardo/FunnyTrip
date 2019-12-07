package cn.huangchengxi.funnytrip.item;

public class TeamInvitationItem {
    private String portraitUrl;
    private String teamID;
    private String teamName;
    private String invitorName;
    private boolean agreed;
    public TeamInvitationItem(String teamID,String teamName,String invitorName,String portraitUrl,boolean agreed){
        this.agreed=agreed;
        this.invitorName=invitorName;
        this.portraitUrl=portraitUrl;
        this.teamID=teamID;
        this.teamName=teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public String getInvitorName() {
        return invitorName;
    }

    public String getTeamID() {
        return teamID;
    }

    public boolean isAgreed() {
        return agreed;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }
}
