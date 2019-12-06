package cn.huangchengxi.funnytrip.item;

import java.io.Serializable;

public class PositionItem implements Serializable {
    private String PosId;
    private String name;
    private Double latitude;
    private Double longitude;
    public PositionItem(String posId,String name,Double latitude,Double longitude){
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