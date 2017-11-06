package com.example.zhf.forcontact;

import android.content.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.baidu.location.*;
import com.baidu.mapapi.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.*;
import com.example.zhf.forcontact.util.GlobleVariable;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private static String TAG = "mainactivity";
    private Toolbar mToolbar;
    private NavigationView mNavgationView;
    private ImageView mNavHeadPic;
    private LinearLayout mNavHeadLinearlayout;
    private FloatingActionButton mFloatingActionButton;
    private  DrawerLayout mDrawerLayout;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private boolean mIsFirstLocate = true;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());           //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);                     //注册监听函数
        SDKInitializer.initialize(getApplicationContext());               // 使用baidumap需要初始化

        setContentView(R.layout.activity_main);

        initView(null);
        mMapView = (MapView) findViewById(R.id.baiduMapView);            //获取地图控件引用
        mBaiduMap = mMapView.getMap();
        initLocation();
        mLocationClient.start();                                    // 开始定位
        mBaiduMap.setMyLocationEnabled(true);


    }



    private void initView(View v){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavgationView = (NavigationView) findViewById(R.id.nav_view);
        View view = mNavgationView.inflateHeaderView(R.layout.nav_header_main);
        mNavHeadLinearlayout =  view.findViewById(R.id.nav_head_layout);
        Log.d(GlobleVariable.TAG + TAG,"mNavHeadLinearlayout = " + mNavHeadLinearlayout);
        mNavHeadLinearlayout.setOnClickListener(this);
        mNavgationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(GlobleVariable.TAG + TAG,"enter onclick  " );
        switch (v.getId()){
            case R.id.nav_head_layout:
                Log.d(GlobleVariable.TAG + TAG,"enter nav_head_laoyout" );

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, LoginActivity.class);
                startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);

    }

   private void initLocation(){
        LocationClientOption option = new LocationClientOption();

       option.setIsNeedAddress(true);  // 获得当前位置详细地址信息
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

       option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

       option.setScanSpan(5000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

       option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

       option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

       option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

       /*option.setIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

       option.setWifiValidTime(5*60*1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位*/

       option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

       mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
   }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){

            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(location);   //定位
            }
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            double latitude = location.getLatitude();        //获取纬度信息
            double longitude = location.getLongitude();      //获取经度信息
            float radius = location.getRadius();             //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();           //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//            Log.d(GlobleVariable.TAG + TAG,"latitude = " + latitude + "\n longitude =  " + longitude + "\n radius = " + radius
//                    +"\n " + location.getCountry() + location.getProvince() + location.getCity() + location.getDistrict() + location.getStreet());
        }
    }

    private void navigateTo(BDLocation location){    //定位到当前位置
        if(mIsFirstLocate){
            LatLng ll =  new LatLng(location.getLatitude(), location.getLongitude());
            /*MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);*/

            MapStatus newMapStatus = new MapStatus.Builder().target(ll).zoom(16f).build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(newMapStatus);
            //改变地图状态
            mBaiduMap.animateMapStatus(mMapStatusUpdate);

            mIsFirstLocate = false;
        }

        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

}
