package cn.huangchengxi.funnytrip.item;

public class TeamPartnerItem {
    private String userId;
    private String userName;
    private String portraitUrl;

    public TeamPartnerItem(String userId,String userName,String portraitUrl){
        this.portraitUrl=portraitUrl;
        this.userId=userId;
        this.userName=userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public String getUserName() {
        return userName;
    }
}
