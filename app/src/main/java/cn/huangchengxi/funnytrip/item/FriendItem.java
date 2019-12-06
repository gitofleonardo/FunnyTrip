package cn.huangchengxi.funnytrip.item;

public class FriendItem {
    private String userID;
    private String userName;
    private String portraitUrl;
    private Character letter;
    private boolean isFriendItem=true;

    public FriendItem(String userID,String userName,String portraitUrl){
        this.userID=userID;
        this.portraitUrl=portraitUrl;
        this.userName=userName;
        isFriendItem=true;
    }
    public FriendItem(Character character){
        this.letter=character;
        isFriendItem=false;
    }
    public String getUserID() {
        return userID;
    }
    public String getUserName() {
        return userName;
    }

    public boolean isFriendItem() {
        return isFriendItem;
    }

    public Character getLetter() {
        return letter;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }
}
