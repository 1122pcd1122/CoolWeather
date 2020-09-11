package activitytest.example.com.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import activitytest.example.com.coolweather.MainActivity;
import activitytest.example.com.coolweather.R;
import activitytest.example.com.coolweather.WeatherActivity;
import activitytest.example.com.coolweather.db.City;
import activitytest.example.com.coolweather.db.County;
import activitytest.example.com.coolweather.db.Province;
import activitytest.example.com.coolweather.util.HttpUtil;
import activitytest.example.com.coolweather.util.Utility;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList =new ArrayList<> (  );

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;


    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的身份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate ( R.layout.choose_area,container,false );

        titleText=(TextView)view.findViewById ( R.id.title_text );
        backButton=(Button)view.findViewById ( R.id.back_button );
        listView=(ListView)view.findViewById ( R.id.list_view );
        adapter=new ArrayAdapter<> ( Objects.requireNonNull ( getContext () ),android.R.layout.simple_list_item_1,dataList );

        listView.setAdapter ( adapter );
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated ( savedInstanceState );
        listView.setOnItemClickListener ( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get ( position );
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get ( position );
                    queryCounties ();
                }else if (currentLevel==LEVEL_COUNTY){
                    String weatherId=countyList.get ( position ).getWeatherId ();
                    if (getActivity ()instanceof MainActivity) {
                        Intent intent = new Intent ( getActivity (), WeatherActivity.class );
                        intent.putExtra ( "weather_id", weatherId );
                        startActivity ( intent );
                        getActivity ().finish ();
                    }else if (getActivity ()instanceof WeatherActivity){
                        WeatherActivity activity=(WeatherActivity)getActivity ();
                        activity.drawerLayout.closeDrawers (  );
                        activity.swipeRefresh.setRefreshing ( true );
                        activity.requestWeather ( weatherId );
                    }
                }

            }
        } );

        backButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities ();
                }else if (currentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        } );

        queryProvinces ();
    }


    /**
     *查询全国所有的省,优先从数据库查询,如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText ( "中国" );
        backButton.setVisibility ( View.GONE );
        provinceList= LitePal.findAll ( Province.class );
        if (provinceList.size ()>0){
            dataList.clear ();
            for (Province province:provinceList) {
                dataList.add ( province.getProvinceName () );
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection ( 0 );
            currentLevel=LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");

        }
    }


    /**
     *查询选中的市内的所有县.优先从数据库查询,如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText ( selectedProvince.getProvinceName () );
        backButton.setVisibility ( View.VISIBLE );
        cityList=LitePal.where ( "provinceid = ?",String.valueOf ( selectedProvince.getProvinceCode () ) ).find ( City.class );
        if (cityList.size ()>0){
            dataList.clear ();
            for (City city :cityList) {
                dataList.add ( city.getCityName () );
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection ( 0 );
            currentLevel=LEVEL_CITY;
        }else {
            int provinceCode=selectedProvince.getProvinceCode ();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer ( address,"city" );
        }
    }

    /**
     *查询选中的省内的所有的市,优先中数据库查询,如果没有查询到在服务器上查询
     */
    private void queryCounties() {
        titleText.setText ( selectedCity.getCityName () );
        backButton.setVisibility ( View.VISIBLE );
        countyList=LitePal.where ( "cityid = ?", String.valueOf ( selectedCity.getCityCode () )).find ( County.class );
        if (countyList.size ()>0){
            dataList.clear ();
            for (County county :countyList) {
                dataList.add ( county.getCountyName () );
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection ( 0 );
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode =selectedProvince.getProvinceCode ();
            int cityCode=selectedCity.getCityCode ();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer ( address,"county" );
        }
    }


    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendHttpRequest ( address, new Callback () {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity ().runOnUiThread ( new Runnable () {
                    @Override
                    public void run() {
                        closeProgressDialog ();
                        Toast.makeText ( getContext (), "加载失败", Toast.LENGTH_SHORT ).show ();
                    }
                } );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseText= Objects.requireNonNull ( response.body () ).string ();
                boolean result=false;
                if ("province".equals ( type )){
                    result= Utility.handleProvinceResponse ( responseText );
                }else if ("city".equals ( type )){
                    result=Utility.handleCityResponse ( responseText ,selectedProvince.getProvinceCode ());
                }else if ("county".equals ( type )){
                    result=Utility.handleCountyResponse ( responseText,selectedCity.getCityCode ());

                }
                closeProgressDialog();
                if (result){
                    Objects.requireNonNull ( getActivity () ).runOnUiThread ( new Runnable () {
                        @Override
                        public void run() {
                            if ("province".equals ( type )){
                                queryProvinces ();
                            }else if ("city".equals ( type )){
                                queryCities ();
                            }else if ("county".equals ( type )){
                                queryCounties ();
                            }
                        }
                    } );
                }
            }
        } );
    }

    private void closeProgressDialog() {
        progressDialog.dismiss ();
    }

    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog=new ProgressDialog ( getActivity () );
            progressDialog.setMessage ( "正在加载. . . ");
            progressDialog.setCanceledOnTouchOutside ( false );
        }
        progressDialog.show ();
    }


}
