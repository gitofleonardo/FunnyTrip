package cn.huangchengxi.funnytrip.item;

public class TipsItem {
    private long time;
    private String name;
    private String url;
    private String id;
    public TipsItem(String id,String name,String url,long time){
        this.name=name;
        this.time=time;
        this.url=url;
        this.id=id;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
