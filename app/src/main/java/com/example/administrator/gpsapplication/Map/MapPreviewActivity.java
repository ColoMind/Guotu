package com.example.administrator.gpsapplication.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.Proximity2DResult;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureResult;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.example.administrator.gpsapplication.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapPreviewActivity extends AppCompatActivity implements OnLongPressListener {
    private MapView mMapView;
    private GraphicsLayer mGraphicsLayer;
    private Callout mCallout;
    private ViewGroup mCalloutContent;
    boolean mIsMapLoaded;
    private List<Point> pointList = new ArrayList<>();
    private Graphic graphic;
    public static int graphicID = -1;//GraphicsLayer中指定Graphic的id
    public static final String TAG = MapPreviewActivity.class.getSimpleName();

    // The query params switching menu items.
    MenuItem mQueryUsMenuItem = null;
    MenuItem mQueryCaMenuItem = null;
    MenuItem mQueryFrMenuItem = null;
    MenuItem mQueryAuMenuItem = null;
    MenuItem mQueryBrMenuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapview);
        mMapView = findViewById(R.id.map_view);
        mGraphicsLayer = new GraphicsLayer();
        //去掉开发者Logo
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");

        addUrlToLayer(getIntent().getStringExtra(getString(R.string.key_save_map_url)));

        // Set the Esri logo to be visible, and enable map to wrap around date line.
        mMapView.setEsriLogoVisible(false);

        mMapView.enableWrapAround(false);
        locatePosition();

        LayoutInflater inflater = getLayoutInflater();

        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

            //
            public void onStatusChanged(Object source, STATUS status) {
                // 检查地图是否成功调用
                if ((source == mMapView) && (status == STATUS.INITIALIZED)) {
                    // Set the flag to true
                    mIsMapLoaded = true;
                }
            }
        });

        mMapView.setOnSingleTapListener(new OnSingleTapListener() {

            @Override
            public void onSingleTap(float x, float y) {
                handleSingleTap(x, y);
            }
        });
        mMapView.setOnLongPressListener(this);

    }

    public void addUrlToLayer(String url) {
        ArcGISTiledMapServiceLayer mTiledLayer = new ArcGISTiledMapServiceLayer(url);
        mMapView.removeAll();
        mMapView.addLayer(mTiledLayer);
        MapOptions options = new MapOptions(MapOptions.MapType.TOPO);
        options.setZoom(13);
        mMapView.setMapOptions(options);
    }

    public void locatePosition() {
        LocationDisplayManager locationDisplayManager = mMapView.getLocationDisplayManager();//获取定位类
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);//设置模式
        locationDisplayManager.start();//开始定位
        Point point = locationDisplayManager.getPoint();
        mMapView.zoomToResolution(point, 0.25);
        mMapView.setScale(3000);
    }

    public void readMapInLocal() {
        File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/shape", "LXDT_XZQH.tpk");
        if (destination.exists()) {
            //存储切片文件路径
            String path = "file:///storage/sdcard0/BJApp/shape/LXDT_XZQH.tpk";
            //声明并实例化ArcGISLocalTiledLayer
            ArcGISLocalTiledLayer localMap = new ArcGISLocalTiledLayer(path);
            //将离线地图加载到MapView中
            mMapView.removeAll();
            mMapView.addLayer(localMap);
        }
    }

    private void handleSingleTap(float x, float y) {
        Point point = mMapView.toMapPoint(x, y);
        mGraphicsLayer.removeAll();
        mMapView.addLayer(mGraphicsLayer);
        pointList.add(point);
        /*String type = graphicTypeSpinner.getSelectedItem().toString().trim();
        switch (type) {
            case "点":
                graphic = new Graphic(point, new SimpleMarkerSymbol(Color.RED,5, SimpleMarkerSymbol.STYLE.CIRCLE));
                mGraphicsLayer.addGraphic(graphic);
                break;
            case "线":
                Polyline polyline = new Polyline();
                if (pointList.size()>1){
                    for(int i=0;i<pointList.size();i++){
                        if (i==0){
                            polyline.startPath(pointList.get(i));
                        }else{
                            polyline.lineTo(pointList.get(i));
                        }
                    }
                }
                graphic = new Graphic(polyline,new SimpleLineSymbol(Color.RED,3, SimpleLineSymbol.STYLE.SOLID));
                mGraphicsLayer.addGraphic(graphic);
                break;
            case "面":*/
        Polygon polygon = new Polygon();
        for (int i = 0; i < pointList.size(); i++) {
            if (i == 0) {
                polygon.startPath(pointList.get(i));
            } else {
                polygon.lineTo(pointList.get(i));
            }
        }
        graphic = new Graphic(polygon, new SimpleFillSymbol(Color.GREEN, SimpleFillSymbol.STYLE.SOLID));
        mGraphicsLayer.addGraphic(graphic);
                /*break;*/
      /*  }*/
    }

    /**
     * Shows the Attribute values for the Graphic in the Callout
     *
     * @param calloutView a callout to show
     * @param graphic     selected graphic
     * @param mapPoint    point to show callout
     */
    /*private void showCallout(Callout calloutView, Graphic graphic, Point mapPoint) {

        // Get the values of attributes for the Graphic
        String cityName = (String) graphic.getAttributeValue("CITY_NAME");
        String countryName = (String) graphic.getAttributeValue("CNTRY_NAME");
        String cityPopulationValue = graphic.getAttributeValue("POP").toString();

        // Set callout properties
        calloutView.setCoordinates(mapPoint);

        // Compose the string to display the results
        StringBuilder cityCountryName = new StringBuilder();
        cityCountryName.append(cityName);
        cityCountryName.append(", ");
        cityCountryName.append(countryName);

        TextView calloutTextLine1 = (TextView) findViewById(R.id.tv_city);
        calloutTextLine1.setText(cityCountryName);

        // Compose the string to display the results
        StringBuilder cityPopulation = new StringBuilder();
        cityPopulation.append(cityPopulationValue);

        TextView calloutTextLine2 = (TextView) findViewById(R.id.tv_pop);
        calloutTextLine2.setText(cityPopulation);
        calloutView.setContent(mCalloutContent);
        calloutView.show();
    }*/

    /**
     * Run the query task on the feature layer and put the result on the map.
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_list, menu);

        // Get the query params menu items.
        mQueryUsMenuItem = menu.getItem(0);
        mQueryCaMenuItem = menu.getItem(1);
        mQueryFrMenuItem = menu.getItem(2);
        mQueryAuMenuItem = menu.getItem(3);
        mQueryBrMenuItem = menu.getItem(4);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu item selection.
        switch (item.getItemId()) {
            case R.id.Query_Township:
                mQueryUsMenuItem.setChecked(true);
                return true;
            case R.id.Query_Image:
                mQueryCaMenuItem.setChecked(true);
                return true;
            case R.id.Query_LandUse:
                mQueryFrMenuItem.setChecked(true);
                return true;
            case R.id.Query_Plan:
                mQueryAuMenuItem.setChecked(true);
                return true;
            case R.id.Query_Dam_Area:
                mQueryBrMenuItem.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onPause() {
        super.onPause();
        mMapView.pause();
    }

    protected void onResume() {
        super.onResume();
        mMapView.unpause();
    }

    @Override
    public boolean onLongPress(float x, float y) {
        int[] id = mGraphicsLayer.getGraphicIDs(x, y, 20);

        if (id.length != 0) {
            graphicID = id[0];
            graphic = mGraphicsLayer.getGraphic(graphicID);
            mGraphicsLayer.updateGraphic(graphicID, graphic);

            //选中ID为GraphicID的graphic后再绑定地图触摸事件
            mMapView.setOnTouchListener(new MyMapOnTouchListener(this, mMapView));
            /*mMapView.setOnTouchListener(new MyMapOnTouchListener(MainMap3Activity.this, mMapView, BJTextview));*/
        } else {
            Log.i(TAG, "未找到Graphic");
        }
        return true;
    }


    public class MyMapOnTouchListener extends MapOnTouchListener {
        private Context context = null;
        private MapView mapEditPolygon;
        /*  private TextView BJTextview;*/
        private int editIndex = -1;
        private Graphic graphicToEdit = mGraphicsLayer.getGraphic(graphicID);
        private Polygon polygonToEdit = (Polygon) graphicToEdit.getGeometry();

        public MyMapOnTouchListener(Context context, MapView view) {
            super(context, view);
            mapEditPolygon = view;
        }

        @Override
        public boolean onDragPointerMove(MotionEvent from, MotionEvent to) throws NullPointerException {
            if (editIndex < 0) {//此时需要获取编辑的节点序号
                if (from != null) {
                    Point ptClick = mapEditPolygon.toMapPoint(from.getX(), from.getY());
                    Proximity2DResult pr = GeometryEngine.getNearestVertex(polygonToEdit, ptClick);
                    editIndex = pr.getVertexIndex();
                    Log.i(TAG, "获取editIndex=" + editIndex);
                } else {
                    Toast.makeText(MapPreviewActivity.this, "请选择点进行编辑", Toast.LENGTH_SHORT).show();
                }
            }

            if (graphicToEdit != null && editIndex >= 0) {
                Point ptTo = mapEditPolygon.toMapPoint(to.getX(), to.getY());
                polygonToEdit.setPoint(editIndex, ptTo);//改变指定节点的坐标
                Log.i(TAG, "改变指定节点的坐标");
            }
            mGraphicsLayer.updateGraphic(graphicID, polygonToEdit);
            return true;
        }

        @Override
        public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
            Log.i(TAG, "重置editindex");
            editIndex = -1;
            if (mGraphicsLayer.getGraphic(graphicID) != null) {
                mGraphicsLayer.updateGraphic(graphicID, graphicToEdit);
            } else {
                Log.i(TAG, "此时无可刷新的图层");
            }
            return true;
        }

    }
}

