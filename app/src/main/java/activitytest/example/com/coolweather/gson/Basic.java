package activitytest.example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 基础类
 */
public class Basic {
    /**
     * 城市名称
     */
    @SerializedName ( "city" )
    public String cityName;
    /**
     * 天气id
     */
    @SerializedName ( "id" )
    public String weatherId;
    /**
     * 更新类-获取更新时间
     */
    public Update update;

    public  class Update{
        @SerializedName ( "loc" )
        public String updateTime;
    }
}
