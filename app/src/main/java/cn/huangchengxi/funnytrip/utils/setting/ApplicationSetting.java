package cn.huangchengxi.funnytrip.utils.setting;

import java.util.ArrayList;

import cn.huangchengxi.funnytrip.item.ClockItem;
import cn.huangchengxi.funnytrip.item.NoteItem;
import cn.huangchengxi.funnytrip.item.WeatherNow;

public class ApplicationSetting {
    private ApplicationSetting(){}

    public static WeatherNow weatherNow=null;
    public static String WEATHER_ID=null;
    //default value
    public static int MAX_NOTE_COUNT=3;
    public static int MAX_CLOCK_COUNT=5;

    public static final ArrayList<NoteItem> notes=new ArrayList<>();
    public static final ArrayList<ClockItem> clocks=new ArrayList<>();
}
