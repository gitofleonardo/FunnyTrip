package cn.huangchengxi.funnytrip.item;

public class FriendRequestItem {
    private String portraitUrl;
    private String uid;
    private String name;
    private String message;
    private long time;
    private boolean agreed;
    public FriendRequestItem(String uid,String portraitUrl,String name,String message,long time,boolean agreed){
        this.agreed=agreed;
        this.message=message;
        this.name=name;
        this.portraitUrl=portraitUrl;
        this.uid=uid;
        this.time=time;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public String getUid() {
        return uid;
    }

    public void setAgreed(boolean agreed) {
        this.agreed = agreed;
    }

    public boolean isAgreed() {
        return agreed;
    }
}
