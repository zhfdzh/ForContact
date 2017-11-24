package com.example.zhf.forcontact.util;

import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.zhf.forcontact.R;
import com.example.zhf.forcontact.overlayutil.DrivingRouteOverlay;
import com.example.zhf.forcontact.overlayutil.TransitRouteOverlay;
import com.example.zhf.forcontact.overlayutil.WalkingRouteOverlay;
import com.tencent.mm.opensdk.utils.Log;

/**
 * Created by zhf on 2017/11/8.
 */

public class BaiduMapUtil {

    private static final String TAG = "BaiduMapUtil";
    private static boolean mIsFirstLocate = true;
    public static String mDefaultCity;
    public static String mLocateStreet;
    public static String mLocateCity;
    public static double mLatitude;
    public static double mLongitude;

    public static boolean mUseDefaultIcon = false;


    public static LocationClient initLocation(LocationClient mLocationClient){
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
        return mLocationClient;
    }

    public static class MyLocationListener extends BDAbstractLocationListener {

        private  BaiduMap baiduMap;

        public void setBaiduMap(BaiduMap mBaiduMap){
            this.baiduMap = mBaiduMap;
        }

        public MyLocationListener(){
            Log.d(GlobleVariable.TAG + TAG,"enter MyLocationListener ");
            mIsFirstLocate = true;
        }

        @Override
        public void onReceiveLocation(BDLocation location){
            Log.d(GlobleVariable.TAG + TAG,"enter MyLocationListener onReceiveLocation");
            Log.d(GlobleVariable.TAG + TAG,"enter MyLocationListener location = " + location);
            Log.d(GlobleVariable.TAG + TAG,"enter MyLocationListener onReceiveLocation: " + (location.getLocType() ));

            if(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation){
                navigateTo(location, baiduMap);   // 定位
            }

            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            mLatitude = location.getLatitude();        //获取纬度信息
            mLongitude = location.getLongitude();      //获取经度信息
            mDefaultCity = location.getProvince();
            mLocateCity = location.getCity();
            mLocateStreet = location.getStreet();
            float radius = location.getRadius();             //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();        //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();           //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
//            Log.d(GlobleVariable.TAG + TAG,"latitude = " + mLatitude + "\n longitude =  " + mLongitude + "\n radius = " + radius
//                    +"\n " + location.getCountry() + location.getProvince() + location.getCity() + location.getDistrict() + location.getStreet());
        }
    }

    private static void navigateTo(BDLocation location , BaiduMap mBaiduMap){    //定位到当前位置
        Log.d(GlobleVariable.TAG + TAG,"enter MyLocationListener navigateTo");

        if(mIsFirstLocate){
            LatLng ll =  new LatLng(location.getLatitude(), location.getLongitude());
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

    // 定制RouteOverly
    public static class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.huaji);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.huaji);
            }
            return null;
        }
    }

    /**
     * 切换路线图标，刷新地图使其生效 注意： 起终点图标使用中心对齐.
     */
    /*
    public void changeRouteIcon(View v) {
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this, "将使用系统起终点图标", Toast.LENGTH_SHORT).show();

        } else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this, "将使用自定义起终点图标", Toast.LENGTH_SHORT).show();

        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }*/

    public static class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.huaji);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.huaji);
            }
            return null;
        }
    }

    public static class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.huaji);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (mUseDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.huaji);
            }
            return null;
        }
    }

}
