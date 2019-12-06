package cn.huangchengxi.funnytrip.databean;

public class UserPropertiesBean {
    private boolean showAddress;
    private boolean showCareer;
    private boolean showGender;
    private boolean showHomeland;
    private boolean showInterest;
    private boolean showBirthday;
    private boolean acceptNewFriend;
    private boolean acceptMessage;
    private boolean acceptTeam;
    private boolean allowViewMomentsForUnknown;
    private boolean allowViewMoments;
    private boolean allowCommentForUnknown;

    public UserPropertiesBean(boolean showAddress,boolean showCareer,boolean showGender,boolean showHomeland,boolean showInterest,boolean showBirthday,boolean acceptNewFriend,boolean acceptMessage,boolean acceptTeam,boolean allowViewMoments,boolean allowViewMomentsForUnknown,boolean allowCommentForUnknown){
        this.showAddress=showAddress;
        this.showCareer=showCareer;
        this.showGender=showGender;
        this.showHomeland=showHomeland;
        this.showInterest=showInterest;
        this.showBirthday=showBirthday;
        this.acceptNewFriend=acceptNewFriend;
        this.acceptMessage=acceptMessage;
        this.acceptTeam=acceptTeam;
        this.allowViewMoments=allowViewMoments;
        this.allowViewMomentsForUnknown=allowViewMomentsForUnknown;
        this.allowCommentForUnknown=allowCommentForUnknown;
    }

    public boolean isAcceptMessage() {
        return acceptMessage;
    }

    public boolean isAcceptNewFriend() {
        return acceptNewFriend;
    }

    public boolean isAcceptTeam() {
        return acceptTeam;
    }

    public boolean isAllowCommentForUnknown() {
        return allowCommentForUnknown;
    }

    public boolean isAllowViewMoments() {
        return allowViewMoments;
    }

    public boolean isAllowViewMomentsForUnknown() {
        return allowViewMomentsForUnknown;
    }

    public boolean isShowAddress() {
        return showAddress;
    }

    public boolean isShowBirthday() {
        return showBirthday;
    }

    public boolean isShowCareer() {
        return showCareer;
    }

    public boolean isShowGender() {
        return showGender;
    }

    public boolean isShowHomeland() {
        return showHomeland;
    }

    public boolean isShowInterest() {
        return showInterest;
    }
}
