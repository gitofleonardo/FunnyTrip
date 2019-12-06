package cn.huangchengxi.funnytrip.databean;

public class UserInformationBean {
    private String uid;
    private String portraitUrl;
    private String nickname;
    private String mail;
    private String address;
    private int gender;
    private long birthday;
    private String homeland;
    private String career;
    private String interest;
    private boolean showAddFriend;
    private boolean showSendMessage;
    private long registerTime;

    public UserInformationBean(String uid,String portraitUrl,String nickname,String mail,String address,String homeland,String career,String interest,int gender,long birthday,long registerTime,boolean showAddFriend,boolean showSendMessage){
        this.address=address;
        this.birthday=birthday;
        this.career=career;
        this.portraitUrl=portraitUrl;
        this.nickname=nickname;
        this.mail=mail;
        this.gender=gender;
        this.homeland=homeland;
        this.interest=interest;
        this.showAddFriend=showAddFriend;
        this.showSendMessage=showSendMessage;
        this.uid=uid;
        this.registerTime=registerTime;
    }
    public UserInformationBean(String uid,String portraitUrl,String nickname,String mail,String address,String homeland,String career,String interest,int gender,long birthday,long registerTime){
        this.address=address;
        this.birthday=birthday;
        this.career=career;
        this.portraitUrl=portraitUrl;
        this.nickname=nickname;
        this.mail=mail;
        this.gender=gender;
        this.homeland=homeland;
        this.interest=interest;
        this.uid=uid;
        this.registerTime=registerTime;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public String getUid() {
        return uid;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public int getGender() {
        return gender;
    }

    public long getBirthday() {
        return birthday;
    }

    public String getAddress() {
        return address;
    }

    public String getCareer() {
        return career;
    }

    public String getHomeland() {
        return homeland;
    }

    public String getInterest() {
        return interest;
    }

    public String getMail() {
        return mail;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean isShowAddFriend() {
        return showAddFriend;
    }

    public boolean isShowSendMessage() {
        return showSendMessage;
    }
}
