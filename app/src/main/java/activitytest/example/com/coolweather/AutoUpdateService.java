package activitytest.example.com.coolweather;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import activitytest.example.com.coolweather.gson.Weather;
import activitytest.example.com.coolweather.util.HttpUtil;
import activitytest.example.com.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      updateWeather ();
      updateBingPic ();
        AlarmManager manager=(AlarmManager)getSystemService ( ALARM_SERVICE );
        int anHour=8 * 60 * 60 * 1000;//这是8小时的毫秒
        long triggerAtTime= SystemClock.elapsedRealtime ()+anHour;
        Intent i=new Intent ( this,AutoUpdateService.class );
        PendingIntent pi=PendingIntent.getActivity ( this,0,i,0 );
        assert manager != null;
        manager.cancel ( pi );
        manager.set ( AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi );

        return super.onStartCommand ( intent, flags, startId );
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences ( this );
        String weatherString=prefs.getString ( "weather",null );
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse ( weatherString );
            String weatherId=weather.basic.weatherId;
            String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId;
            HttpUtil.sendHttpRequest ( weatherUrl, new Callback () {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace ();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseText=response.body ().string ();
                    Weather weather=Utility.handleWeatherResponse ( responseText );
                    if (weather!=null && "OK".equals ( weather.status )){
                        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences ( AutoUpdateService.this ).edit ();
                        editor.putString ( "weather",responseText );
                        editor.apply ();

                    }

                }
            } );
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic(){
            String requestBingPic="http://guolin.tech/api/bing_pic";
            HttpUtil.sendHttpRequest ( requestBingPic, new Callback () {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace ();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String bingPic=response.body ().string ();
                        SharedPreferences.Editor editor=  PreferenceManager.getDefaultSharedPreferences ( AutoUpdateService.this).edit ();
                        editor.putString ( "bing_pic",bingPic );
                        editor.apply ();
                }
            } );
    }
}
