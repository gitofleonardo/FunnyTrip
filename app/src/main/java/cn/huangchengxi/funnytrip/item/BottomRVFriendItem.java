package cn.huangchengxi.funnytrip.item;

public class BottomRVFriendItem {
    private String userId;
    private String userName;
    private boolean isCheck;
    private String portraitUrl;

    public BottomRVFriendItem(String userId,String userName,String portraitUrl,boolean isCheck){
        this.isCheck=isCheck;
        this.userId=userId;
        this.userName=userName;
        this.portraitUrl=portraitUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
