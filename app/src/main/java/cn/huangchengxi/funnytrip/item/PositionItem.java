package cn.huangchengxi.funnytrip.item;

public class PositionItem {
    private String PosId;
    private String name;
    private double latitude;
    private double longitude;
    public PositionItem(String posId,String name,double latitude,double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
        this.name=name;
        this.PosId=posId;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPosId() {
        return PosId;
    }
}