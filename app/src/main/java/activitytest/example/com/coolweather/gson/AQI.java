package activitytest.example.com.coolweather.gson;

/**
 * 空气质量指数-AQI
 */
public class AQI {

    /**
     * 得到AQI的城市
     */
    public AQICITY city;
    public  class AQICITY{
        public String aqi;
        public String pm25;
    }
}
