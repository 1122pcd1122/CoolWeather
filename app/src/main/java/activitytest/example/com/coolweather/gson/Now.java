package activitytest.example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实时温度
 */
public class Now {
    @SerializedName ("tmp")
    public String temperature;
    /**
     * 天气情况
     */
    @SerializedName ( "cond" )
    public More more;

    public class More{
        @SerializedName ( "txt" )
        public String info;
    }
}
