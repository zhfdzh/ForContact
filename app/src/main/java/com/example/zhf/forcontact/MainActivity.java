package com.example.zhf.forcontact;

import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.*;
import android.support.annotation.RequiresApi;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.baidu.location.*;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.*;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.example.zhf.forcontact.overlayutil.DrivingRouteOverlay;
import com.example.zhf.forcontact.overlayutil.OverlayManager;
import com.example.zhf.forcontact.overlayutil.PoiOverlay;
import com.example.zhf.forcontact.overlayutil.TransitRouteOverlay;
import com.example.zhf.forcontact.overlayutil.WalkingRouteOverlay;
import com.example.zhf.forcontact.util.ActionBarUtil;
import com.example.zhf.forcontact.util.BaiduMapUtil;
import com.example.zhf.forcontact.util.GlobleVariable;
import com.tencent.mm.opensdk.utils.*;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Objects;

import static com.example.zhf.forcontact.util.BaiduMapUtil.initLocation;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private static String TAG = "MainActivity";
    private Toolbar mToolbar;
    private ActionBar mActionbar;
    private NavigationView mNavgationView;
    private ImageView mNavHeadPic;
    private LinearLayout mNavHeadLinearlayout;
    private FloatingActionButton mFloatingActionButton;
    private DrawerLayout mDrawerLayout;
    private MapView mMapView;
    private PopupWindow mPopupWindow;
    private Button mNavRouteButton;
    private LinearLayout mNavRoutelinearlayout;

    private BaiduMap mBaiduMap;
    private RouteLine mRoute = null;
    private PlanNode mStartPlanNode;
    private PlanNode mEndPlanNode;
    private OverlayManager mRouteOverlay = null;
    private SharedPreferences mSharePref;

    private boolean mIsFirstLocate = true;
    public LocationClient mLocationClient = null;    // 定位的核心类:LocationClient
    private BaiduMapUtil.MyLocationListener myListener = new BaiduMapUtil.MyLocationListener(); // 定位的回调接口
    private PoiSearch mPoiSearch;// poi检索核心类
    private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());           //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);                     //注册监听函数
//        SDKInitializer.initialize(getApplicationContext());               // 使用baidumap需要初始化
        setContentView(R.layout.activity_main);
        settingActionBar();
        initView(null);
        mMapView = (MapView) findViewById(R.id.baiduMapView);            // 获取地图控件引用
        mBaiduMap = mMapView.getMap();
        myListener.setBaiduMap(mBaiduMap);
        initLocation(mLocationClient);
        Log.d("MainActivity","before mLocationClient.start(); ");

        mLocationClient.start();                                    // 开始定位
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setOnMapClickListener(mapClickListener);              //地图点击事件处理

        mSearch = RoutePlanSearch.newInstance();             // 初始化搜索模块
        mSearch.setOnGetRoutePlanResultListener(mRoutePlanResultListener);       // 注册事件监听

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(mPoiSearchListener);

    }

    private void settingActionBar(){
      /*  mActionbar = getSupportActionBar();
        if(mActionbar != null){
            mActionbar .setDisplayShowTitleEnabled(false);
        }*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBarUtil.setStatusBarUpper(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView(View v){
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                mNavRoutelinearlayout = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.nav_route_popwindow,null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                mPopupWindow = new PopupWindow(mNavRoutelinearlayout,ViewGroup.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
                mPopupWindow.showAtLocation(findViewById(R.id.drawer_layout),Gravity.TOP,50,50);
                if(mSharePref == null){
                    mSharePref = getPreferences(MODE_PRIVATE);
                }
                ((EditText) mNavRoutelinearlayout.findViewById(R.id.search_start)).setText(mSharePref.getString(GlobleVariable.SEARCH_START,""));
                ((EditText) mNavRoutelinearlayout.findViewById(R.id.search_end)).setText(mSharePref.getString(GlobleVariable.SEARCH_END,""));

                Button driveButton = ((Button) mNavRoutelinearlayout.findViewById(R.id.route_drive));
                Button walkButton = ((Button) mNavRoutelinearlayout.findViewById(R.id.route_walk));
                Button transitButton = ((Button) mNavRoutelinearlayout.findViewById(R.id.route_transit));
                driveButton.setOnClickListener(mSearchRouteClickListener);
                walkButton.setOnClickListener(mSearchRouteClickListener);
                transitButton.setOnClickListener(mSearchRouteClickListener);

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
//        mNavgationView.setItemTextColor(getResources().getColorStateList(R.drawable.nav_menu_item_selector, null));         // 设置菜单选中字体颜色
//        mNavgationView.setItemIconTintList(getResources().getColorStateList(R.drawable.nav_menu_item_selector, null));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(GlobleVariable.TAG  + TAG,"enter onNewIntent");
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        double latitude = bundle.getDouble(GlobleVariable.LATITUDE);
        double longitude = bundle.getDouble(GlobleVariable.LONGITUDE);
        Log.d(GlobleVariable.TAG  + TAG,"onNewIntent:   latitude = " + latitude + "  longitude =  " + longitude );
        mStartPlanNode = PlanNode.withLocation(new LatLng(BaiduMapUtil.mLatitude,BaiduMapUtil.mLongitude));
        mEndPlanNode = PlanNode.withLocation(new LatLng(latitude,longitude));
        mSearch.drivingSearch((new DrivingRoutePlanOption()).from(mStartPlanNode).to(mEndPlanNode));
    }

    /**
     * 城市内搜索
     */
    private void citySearch(int page) {
        // 设置检索参数
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        Log.e(GlobleVariable.TAG,"mLocateCity = "+BaiduMapUtil.mLocateCity);
        citySearchOption.city("上海");// 城市
        citySearchOption.keyword("景点");// 关键字
        citySearchOption.pageCapacity(20);// 默认每页10条
        citySearchOption.pageNum(page);// 分页编号
        // 发起检索请求
        mPoiSearch.searchInCity(citySearchOption);
    }

    /**
     * 范围检索
     */
    private void boundSearch(int page) {
        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
        LatLng southwest = new LatLng(BaiduMapUtil.mLatitude - 0.01, BaiduMapUtil.mLongitude - 0.012);// 西南
        LatLng northeast = new LatLng(BaiduMapUtil.mLatitude + 0.01, BaiduMapUtil.mLongitude + 0.012);// 东北
        LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
                .include(northeast).build();// 得到一个地理范围对象
        boundSearchOption.bound(bounds);// 设置poi检索范围
        boundSearchOption.keyword("娱乐");// 检索关键字
        boundSearchOption.pageNum(page);
        mPoiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
    }

    /**
     * 附近检索
     */
    private void nearbySearch(int page) {
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(new LatLng(BaiduMapUtil.mLatitude, BaiduMapUtil.mLongitude));
        nearbySearchOption.keyword("美食");
        nearbySearchOption.radius(5000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
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

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_friend_list) {
            Intent intent = new Intent();
            intent.setClass(this,FriendListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            citySearch(0);
        } else if (id == R.id.nav_slideshow) {
            boundSearch(0);
        } else if (id == R.id.nav_manage) {
            nearbySearch(0);
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
        mSearch.destroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void searchButtonProcess(View v) {
        Log.d(GlobleVariable.TAG + TAG,"enter searchButtonProcess  " );

        // 重置浏览节点的路线数据
        mRoute = null;
        mBaiduMap.clear();
        // 处理搜索按钮响应
        String search_start = ((EditText)mNavRoutelinearlayout.findViewById(R.id.search_start)).getText().toString();
        String search_end = ((EditText)mNavRoutelinearlayout.findViewById(R.id.search_end)).getText().toString();

        if(search_start.equals("")){
            mStartPlanNode = PlanNode.withLocation(new LatLng(BaiduMapUtil.mLatitude,BaiduMapUtil.mLongitude));
        }else {
            mStartPlanNode = PlanNode.withCityNameAndPlaceName(BaiduMapUtil.mLocateStreet,search_start);
        }
        if(search_end.equals("")){
            Toast.makeText(MainActivity.this,"请输入终点",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mSharePref == null){
            mSharePref = getPreferences(MODE_PRIVATE);
        }

        SharedPreferences.Editor editor = mSharePref.edit();
        editor.putString(GlobleVariable.SEARCH_START,search_start);
        editor.putString(GlobleVariable.SEARCH_END,search_end);
        editor.apply();

        mEndPlanNode = PlanNode.withCityNameAndPlaceName(BaiduMapUtil.mLocateStreet,search_end);
        Log.d(GlobleVariable.TAG + TAG,"enter searchButtonProcess:  locateStreet = " + BaiduMapUtil.mLocateStreet + " search_start = " + search_start +  " search_end = " + search_end);

        if (v.getId() == R.id.route_drive) {
//            mSearch.drivingSearch((new DrivingRoutePlanOption()).from(mStartPlanNode).currentCity(BaiduMapUtil.mDefaultCity).to(mEndPlanNode));
            mSearch.drivingSearch((new DrivingRoutePlanOption()).from(mStartPlanNode).to(mEndPlanNode));

        } else if (v.getId() == R.id.route_transit) {
            mSearch.transitSearch((new TransitRoutePlanOption()).from(mStartPlanNode).city(BaiduMapUtil.mDefaultCity).to(mEndPlanNode));
//            mSearch.transitSearch((new TransitRoutePlanOption()).from(mStartPlanNode).to(mEndPlanNode));

        } else if (v.getId() == R.id.route_walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption()).from(mStartPlanNode).to(mEndPlanNode));

        }
    }

    private View.OnClickListener mSearchRouteClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this,"搜索路线",Toast.LENGTH_SHORT).show();
            searchButtonProcess(v);
        }
    };

    private OnGetPoiSearchResultListener mPoiSearchListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null
                    || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(MainActivity.this, "未找到结果",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                mBaiduMap.clear();
                MyPoiOverlay poiOverlay = new MyPoiOverlay(mBaiduMap);
                poiOverlay.setData(poiResult);// 设置POI数据
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.addToMap();// 将所有的overlay添加到地图上
                poiOverlay.zoomToSpan();
                int totalPage = poiResult.getTotalPageNum();// 获取总分页数
                Toast.makeText(MainActivity.this, "总共查到" + poiResult.getTotalPoiNum() + "个兴趣点, 分为" + totalPage + "页", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果",
                        Toast.LENGTH_SHORT).show();
            } else {// 正常返回结果的时候，此处可以获得很多相关信息
                Toast.makeText(MainActivity.this, poiDetailResult.getName() + ": " + poiDetailResult.getAddress(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private OnGetRoutePlanResultListener mRoutePlanResultListener = new  OnGetRoutePlanResultListener(){
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
            Log.d(GlobleVariable.TAG + TAG,"enter onGetWalkingRouteResult:  walkingRouteResult = " + walkingRouteResult);
            if(walkingRouteResult != null){
                Log.d(GlobleVariable.TAG + TAG,"enter onGetWalkingRouteResult:  " + (walkingRouteResult.error +" == "+ SearchResult.ERRORNO.NO_ERROR));
            }

            if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                if(walkingRouteResult == null) return;
            }
            if (walkingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                SuggestAddrInfo suggestAddrInfo = walkingRouteResult.getSuggestAddrInfo();
                Log.d(GlobleVariable.TAG + TAG," onGetWalkingRouteResult:   getSuggestStartCity = "  + suggestAddrInfo.getSuggestStartCity() +
                        "  getSuggestStartNode = "  + suggestAddrInfo.getSuggestStartNode() +
                        "  getSuggestEndCity = "  + suggestAddrInfo.getSuggestEndCity()  +
                        "  getSuggestEndNode = "  + suggestAddrInfo.getSuggestEndNode() +
                        "  getSuggestWpCity = "  + suggestAddrInfo.getSuggestWpCity() +
                        "  suggestAddrInfo.toString() = "  + suggestAddrInfo.toString()
                );
            }else if (walkingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                nodeIndex = -1;
                mRoute = walkingRouteResult.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new BaiduMapUtil.MyWalkingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                mRouteOverlay = overlay;
                overlay.setData(walkingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
            Log.d(GlobleVariable.TAG + TAG,"enter onGetTransitRouteResult:  transitRouteResult = "  + transitRouteResult);
            if(transitRouteResult != null){
                Log.d(GlobleVariable.TAG + TAG,"enter onGetTransitRouteResult:  " + (transitRouteResult.error +" == "+ SearchResult.ERRORNO.NO_ERROR));
            }

            if (transitRouteResult == null || transitRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                if(transitRouteResult == null) return;
            }
            if (transitRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                SuggestAddrInfo suggestAddrInfo = transitRouteResult.getSuggestAddrInfo();
                Log.d(GlobleVariable.TAG + TAG," onGetTransitRouteResult:   getSuggestStartCity = "  + suggestAddrInfo.getSuggestStartCity()+
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestStartNode().get(0).name +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestEndCity().get(0).describeContents()  +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestEndNode().get(0).name +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestWpCity().get(0).get(0).describeContents() +
                        "  getSuggestStartCity = "  + suggestAddrInfo.toString()
                );


            }else if (transitRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                nodeIndex = -1;
                mRoute = transitRouteResult.getRouteLines().get(0);
                TransitRouteOverlay overlay = new BaiduMapUtil.MyTransitRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                mRouteOverlay = overlay;
                overlay.setData(transitRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            Log.d(GlobleVariable.TAG + TAG,"enter onGetDrivingRouteResult:  drivingRouteResult = "  + drivingRouteResult);
            if(drivingRouteResult != null){
                Log.d(GlobleVariable.TAG + TAG,"enter onGetDrivingRouteResult:  " + (drivingRouteResult.error +" == "+ SearchResult.ERRORNO.NO_ERROR));
            }

            if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                Toast.makeText(MainActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
                if(drivingRouteResult == null) return;
            }
            if (drivingRouteResult.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
                // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
                SuggestAddrInfo suggestAddrInfo = drivingRouteResult.getSuggestAddrInfo();
                Log.d(GlobleVariable.TAG + TAG," onGetDrivingRouteResult:   getSuggestStartCity = "  + suggestAddrInfo.getSuggestStartCity() +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestStartNode().get(0).name +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestEndCity().get(0).describeContents()  +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestEndNode().get(0).name +
                        "  getSuggestStartCity = "  + suggestAddrInfo.getSuggestWpCity().get(0).get(0).describeContents() +
                        "  getSuggestStartCity = "  + suggestAddrInfo.toString()
                );

            }else if (drivingRouteResult.error == SearchResult.ERRORNO.NO_ERROR) {
//                nodeIndex = -1;
                mRoute = drivingRouteResult.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new BaiduMapUtil.MyDrivingRouteOverlay(mBaiduMap);
                mRouteOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    };

    private BaiduMap.OnMapClickListener mapClickListener = new BaiduMap.OnMapClickListener(){
        @Override
        public void onMapClick(LatLng latLng) {

        }

        @Override
        public boolean onMapPoiClick(MapPoi mapPoi) {
            return false;
        }
    };

    private class MyPoiOverlay extends PoiOverlay {
        public MyPoiOverlay(BaiduMap arg0) {
            super(arg0);
        }
        @Override
        public boolean onPoiClick(int arg0) {
            super.onPoiClick(arg0);
            PoiInfo poiInfo = getPoiResult().getAllPoi().get(arg0);
            // 检索poi详细信息
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption()
                    .poiUid(poiInfo.uid));
            return true;
        }
    }

}
