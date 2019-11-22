package cn.huangchengxi.funnytrip.item;

public class TipsItem {
    private long time;
    private String name;
    private String url;
    public TipsItem(String name,String url,long time){
        this.name=name;
        this.time=time;
        this.url=url;
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
