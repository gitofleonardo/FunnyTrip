package cn.huangchengxi.funnytrip.item;

public class InterestItem {
    private String name;
    private boolean isSelected;

    public InterestItem(String name,boolean isSelected){
        this.name=name;
        this.isSelected=isSelected;
    }
    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
