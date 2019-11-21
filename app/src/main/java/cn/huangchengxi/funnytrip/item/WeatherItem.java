package cn.huangchengxi.funnytrip.item;

public class WeatherItem {
    private String name;
    private String id;
    private String weatherID;
    public WeatherItem(String name,String id){
        this.id=id;
        this.name=name;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getWeatherID() {
        return weatherID;
    }
}
