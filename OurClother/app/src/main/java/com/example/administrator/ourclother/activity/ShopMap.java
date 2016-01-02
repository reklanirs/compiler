package com.example.administrator.ourclother.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.example.administrator.ourclother.util.SysApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;

import com.baidu.mapapi.cloud.Bounds;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.PoiOverlay;

import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKPoiInfo;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.administrator.ourclother.R;

import java.util.ArrayList;

public class ShopMap extends Activity {
    private Toast mToast;
    private BMapManager mBMapManager;
    private MapView mMapView = null;
    private MapController mMapController = null;
    private MKSearch mMKSearch;
    private String uname;
    /**
     * 定位SDK的核心类
     */
    private LocationClient mLocClient;
    /**
     * 用户位置信息
     */
    private LocationData mLocData;
    /**
     * 我的位置图层
     */
    private LocationOverlay myLocationOverlay = null;
    /**
     * 弹出窗口图层
     */
    private PopupOverlay mPopupOverlay  = null;
    private boolean isFirstLoc = true;//是否首次定位
    /**
     * 弹出窗口图层的View
     */
    private View mPopupView;
    private BDLocation location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用地图sdk前需先初始化BMapManager，这个必须在setContentView()先初始化
        mBMapManager = new BMapManager(this);
        //第一个参数是API key,
        //第二个参数是常用事件监听，用来处理通常的网络错误，授权验证错误等，你也可以不添加这个回调接口
        mBMapManager.init("ugBm6jnCr8k136No6PzvFi1h", new MKGeneralListenerImpl());
        setContentView(R.layout.activity_shop_map);
        mMapView = (MapView) findViewById(R.id.bmapView); //获取百度地图控件实例
        mMapController = mMapView.getController(); //获取地图控制器
        mMapController.enableClick(true);   //设置地图是否响应点击事件
        mMapController.setZoom(17);   //设置地图缩放级别
        mMapView.setBuiltInZoomControls(true);   //显示内置缩放控件
        //mMapView.setTraffic(true);  //设置交通信息图
        //mMapView.setSatellite(true);  //设置卫星图
        //mMapController.setOverlooking(-45);  //设置地图俯视角度 ，范围：0~ -45
        mPopupView = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);
        //实例化弹出窗口图层
        mPopupOverlay = new PopupOverlay(mMapView ,new PopupClickListener() {
            /**
             * 点击弹出窗口图层回调的方法
             */
            @Override
            public void onClickedPopup(int arg0) {
                //隐藏弹出窗口图层
                mPopupOverlay.hidePop();
            }
        });
        SysApplication.getInstance().addActivity(this);
        System.out.println("shop出现！！！！！！！！！！！！");
        //实例化定位服务，LocationClient类必须在主线程中声明
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(new BDLocationListenerImpl());//注册定位监听接口

        /**
         * LocationClientOption 该类用来设置定位SDK的定位方式。
         */
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开GPRS
        option.setAddrType("all");//返回的定位结果包含地址信息
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setPriority(LocationClientOption.GpsFirst); // 设置GPS优先
        option.setScanSpan(5000); //设置发起定位请求的间隔时间为5000ms
        option.disableCache(false);//禁止启用缓存定位
        option.setPoiNumber(5);    //最多返回POI个数
        option.setPoiDistance(1000); //poi查询距离
        option.setPoiExtraInfo(true);  //是否需要POI的电话和地址等详细信息
        mLocClient.setLocOption(option);  //设置定位参数
        mLocClient.start();  // 调用此方法开始定位
        //定位图层初始化
        myLocationOverlay = new LocationOverlay(mMapView);
        //实例化定位数据，并设置在我的位置图层
        mLocData = new LocationData();
        myLocationOverlay.setData(mLocData);
        //添加定位图层
        mMapView.getOverlays().add(myLocationOverlay);
        //修改定位数据后刷新图层生效
        mMapView.refresh();

        mMKSearch = new MKSearch();
        mMKSearch.init(mBMapManager, new MySearchListener());
        //点击搜索衣店按钮
        findViewById(R.id.search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                findShop();
            }
        });
        uname=this.getIntent().getStringExtra("loginId");

    }
 @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        getMenuInflater().inflate(R.menu.activity_menu, menu);

        return true;
    }
      @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            switch (item.getItemId()) {

                case R.id.search:
                    Toast.makeText(getApplicationContext(), "退出……", Toast.LENGTH_SHORT).show();

                            SysApplication.getInstance().exit();


                    return true;

            }
            return false;
        }
    /**
     * 定位接口，需要实现两个方法
     * @author xiaanming
     *
     */
    public class BDLocationListenerImpl implements BDLocationListener {
        /**
         * 接收异步返回的定位结果，参数是BDLocation类型参数
         */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            ShopMap.this.location = location;
            mLocData.latitude = location.getLatitude();
            mLocData.longitude = location.getLongitude();
            //如果不显示定位精度圈，将accuracy赋值为0即可
            mLocData.accuracy = location.getRadius();
            mLocData.direction = location.getDerect();

            //将定位数据设置到定位图层里
            myLocationOverlay.setData(mLocData);
            //更新图层数据执行刷新后生效
            mMapView.refresh();

            if(isFirstLoc){
                //将给定的位置点以动画形式移动至地图中心
                mMapController.animateTo(new GeoPoint(
                        (int) (location.getLatitude() * 1e6), (int) (location
                        .getLongitude() * 1e6)));
                showPopupOverlay(location);
            }
            isFirstLoc = false;
        }

        /**
         * 接收异步返回的POI查询结果，参数是BDLocation类型参数
         */
        @Override
        public void onReceivePoi(BDLocation poiLocation) {
        }

    }

    //继承MyLocationOverlay重写dispatchTap方法。用于处理弹出窗口
    private class LocationOverlay extends MyLocationOverlay{
        public LocationOverlay(MapView arg0) {
            super(arg0);
        }
        /**
         * 在“我的位置”坐标上处理点击事件。
         */
        @Override
        protected boolean dispatchTap() {
            //点击我的位置显示PopupOverlay
            showPopupOverlay(location);
            return super.dispatchTap();
        }
    }
    /**
     * 显示弹出窗口图层PopupOverlay
     * @param location
     */
    private void showPopupOverlay(BDLocation location){
        TextView popText =((TextView)mPopupView.findViewById(R.id.textcache));
        popText.setText("[我的位置]\n" + location.getAddrStr());
        mPopupOverlay.showPopup(getBitmapFromView(popText),
                new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()*1e6)),
                15);

    }

    /**
     * 用于处理搜索衣店的请求
     */
    public void findShop(){
        mMKSearch.setPoiPageCapacity(10);  //每页返回POI数
        mMKSearch.poiSearchInCity("南京", "麦当劳");
    }

    /**
     * 显示Toast消息
     * @param msg
     */
    private void showToast(String msg){
        if(mToast == null){
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 将View转换成Bitmap的方法
     * @param view
     * @return
     */
    public static Bitmap getBitmapFromView(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        return view.getDrawingCache();
    }

    /**
     * 常用事件监听，用来处理通常的网络错误，授权验证错误等
     * @author xiaanming
     *
     */
    public class MKGeneralListenerImpl implements MKGeneralListener{
        /**
         * 一些网络状态的错误处理回调函数
         */
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                showToast("您的网络出错啦！");
            }
        }
        /**
         * 授权错误的时候调用的回调函数
         */
        @Override
        public void onGetPermissionState(int iError) {
            if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
                showToast("API KEY错误, 请检查！");
            }
        }
    }

    @Override
    protected void onResume() {
        //MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
        mMapView.destroy();
        //退出应用调用BMapManager的destroy()方法
        if(mBMapManager != null){
            mBMapManager.destroy();
            mBMapManager = null;
        }
        //退出时销毁定位
        if (mLocClient != null){
            mLocClient.stop();
        }
        super.onDestroy();
    }

    public class MySearchListener implements MKSearchListener {
        /**
         * 根据经纬度搜索地址信息结果
         * 同时mMKSearch.geocode(city, city)搜索城市返回至该函数
         *
         * @param result 搜索结果
         * @param iError 错误号（0表示正确返回）
         */
        @Override
        public void onGetAddrResult(MKAddrInfo result, int iError) {
        }

        /**
         * POI搜索结果（范围检索、城市POI检索、周边检索）
         *
         * @param result 搜索结果
         * @param type   返回结果类型（11,12,21:poi列表 7:城市列表）
         * @param iError 错误号（0表示正确返回）
         */
        @Override
        public void onGetPoiResult(MKPoiResult result, int type, int iError) {
            if (result == null) {
                return;
            }
            //获取POI并显示
            PoiOverlay poioverlay = new MyPoiOverlay(ShopMap.this, mMapView,mMKSearch);
            poioverlay.setData(result.getAllPoi());  //设置搜索到的POI数据
            mMapView.getOverlays().add(poioverlay);   //兴趣点标注在地图上
            mMapView.refresh();
            //设置其中一个搜索结果所在地理坐标为地图的中心
            if (result.getNumPois() > 0) {
                MKPoiInfo poiInfo = result.getPoi(0);
                mMapController.setCenter(poiInfo.pt);
            }
        }
        /**
         * 公交换乘路线搜索结果
         *
         * @param result 搜索结果
         * @param iError 错误号（0表示正确返回）
         */
        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult mkDrivingRouteResult, int i) {

        }

        /**
         * 步行路线搜索结果
         *
         * @param result 搜索结果
         * @param iError 错误号（0表示正确返回）
         */
        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetPoiDetailSearchResult(int arg0, int arg1) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onGetShareUrlResult(MKShareUrlResult mkShareUrlResult, int i, int i2) {

        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
            // TODO Auto-generated method stub
        }
    }


    public class MyPoiOverlay extends PoiOverlay {
        MKSearch mSearch;
        public MyPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
            super(activity, mapView);
            mSearch = search;
        }

        @Override
        protected boolean onTap(int i) {
            super.onTap(i);
            MKPoiInfo shopinfo = getPoi(i);
            ToShop(shopinfo.name);
            System.out.println(shopinfo.name+"      ");
            return true;
        }
    }

    public void ToShop(String shopname){
        Intent intent = new Intent(this,ShopClotherActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("loginId", uname);

        intent.putExtras( bundle);
        startActivity(intent);
    }
}