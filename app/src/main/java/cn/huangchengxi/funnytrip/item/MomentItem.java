package cn.huangchengxi.funnytrip.item;

public class MomentItem {
    private String momentId;
    private String senderId;
    private String name;
    private long time;
    private String content;
    private String imgUrl;

    public MomentItem(String momentId,String senderId,String name,long time,String content,String imgUrl){
        this.content=content;
        this.momentId=momentId;
        this.name=name;
        this.time=time;
        this.senderId=senderId;
        this.imgUrl=imgUrl;
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getMomentId() {
        return momentId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
