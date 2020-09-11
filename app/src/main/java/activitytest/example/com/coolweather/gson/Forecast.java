package activitytest.example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 天气预报
 */
public class Forecast {

    /**
     *时间
     */
    public String date;

    /**
     * 温度
     */
    @SerializedName ( "tmp" )
    public Temperature temperature;
    /**
     * 天气情况
     */
    @SerializedName ( "cond" )
    public More more;


    public class Temperature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName ( "txt_d" )
        public String info;
    }
}
