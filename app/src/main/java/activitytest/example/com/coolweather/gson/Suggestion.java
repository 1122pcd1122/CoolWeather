package activitytest.example.com.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 建议类
 */
public class Suggestion {
    /**
     * 舒适度建议类
     */
    @SerializedName ( "comf" )
        public Comfort comfort;
    /**
     * 洗车建议类
     */
    @SerializedName ( "cw" )
        public CarWash carWash;
    /**
     * 运动建议类
     */

        public Sport sport;


        public class Comfort{
            @SerializedName ( "txt" )
            public String info;
        }

        public class CarWash{
            @SerializedName ( "txt" )
            public String info;
        }
        public class Sport{
            @SerializedName ( "txt" )
            public String info;
        }


}
