package activitytest.example.com.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import activitytest.example.com.coolweather.db.City;
import activitytest.example.com.coolweather.db.County;
import activitytest.example.com.coolweather.db.Province;
import activitytest.example.com.coolweather.gson.Weather;

public class Utility {
    /**
     * @param response 请求的数据
     * @return 返回是否解析成功
     */
    /* 解析省城的数据并存入到数据库中 */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty ( response )) {
            try {
                JSONArray allProvinces = new JSONArray ( response );
                for (int i = 0; i < allProvinces.length (); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject ( i );
                    Province province = new Province ();
                    province.setProvinceCode ( provinceObject.getInt ( "id" ) );
                    province.setProvinceName ( provinceObject.getString ( "name" ) );
                    province.save ();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        return false;
    }

    /**
     * @param response 请求的数据
     * @param provinceId 省的id
     * @return 是否解析成功
     */
    /* 解析城市的数据并存入到数据库中 */
    public static boolean handleCityResponse(String response,int provinceId){

        if (!TextUtils.isEmpty ( response )){
            try {
                JSONArray allCites=new JSONArray ( response );
                for (int i = 0; i < allCites.length (); i++) {
                    City city=new City ();
                    JSONObject cityObject=allCites.getJSONObject (i);
                    city.setCityName ( cityObject.getString ( "name" ) );
                    city.setCityCode ( cityObject.getInt ( "id" ) );
                    city.setProvinceId ( provinceId );
                    city.save ();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }

        return false;
    }

    /* 解析县城的数据并存入到数据库中 */
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty ( response )){
            try {
                JSONArray jsonArray=new JSONArray ( response );
                for (int i = 0; i < jsonArray.length (); i++) {
                    JSONObject jsonObject=jsonArray.getJSONObject ( i );
                    County county=new County ();
                    county.setCountyName ( jsonObject.getString ( "name" ) );
                    county.setWeatherId ( jsonObject.getString ( "weather_id" ) );
                    county.setCityId ( cityId );
                    county.save ();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace ();
            }

        }

        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject=new JSONObject ( response );
            JSONArray jsonArray=jsonObject.getJSONArray ( "HeWeather" );
            String weatherContent = jsonArray.getJSONObject ( 0 ).toString ();
            return new Gson ().fromJson ( weatherContent,Weather.class );
        }catch (Exception e){
            e.printStackTrace ();
        }

        return null;
    }

}
