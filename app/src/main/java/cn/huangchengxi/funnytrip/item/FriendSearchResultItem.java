package cn.huangchengxi.funnytrip.item;

public class FriendSearchResultItem {
    private String name;
    private String id;
    private String portraitUrl;
    public FriendSearchResultItem(String id,String name,String portraitUrl){
        this.id=id;
        this.name=name;
        this.portraitUrl=portraitUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }
}
