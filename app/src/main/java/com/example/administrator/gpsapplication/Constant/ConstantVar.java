package com.example.administrator.gpsapplication.Constant;


import android.os.Environment;

public class ConstantVar {

    //public final static String HOST="server.lbschina.com.cn";
//	public final static String HOST="192.168.1.155";
    public final static String HOST = "61.159.147.194";
    //    public final static String HOST="172.71.0.3";
    public final static String MAPPORT = "6080";
    //    public final static String IISPORT="8081";
    public final static String IISPORT = "4083";

    //空间参考系代号
    public final static int WGS84 = 4326;
    public final static int Xian_1980_3_Degree_GK_Zone_35 = 2359;
    public final static int WGS_1984_Web_Mercator_Auxiliary_Sphere = 102100;

    //登录地址
    //http://server.lbschina.com.cn:6081/server/WebSystemManage.asmx
    public static String LOGINURL = "http://" + HOST + ":" + IISPORT + "/server/WebSystemManage.asmx";
    //地质灾害预警接口
    public static String DZZHYJ = "http://" + HOST + ":" + IISPORT + "/server/WebDisasterTask.asmx";
    //应急值守报警信息
    public static String YJZS = "http://" + HOST + ":" + IISPORT + "/server/WebEmgDutyWarn.asmx";

    //地质灾害
    public static String DZZHQUERYURL = "http://" + HOST + ":" + IISPORT + "/server/WebGeologicalDisasters.asmx";//添加保存图片(地质灾害)
    public static String UPLOAD = "http://" + HOST + ":" + IISPORT + "/server/WebFileUpLoad.asmx";//图片上传
    public static String PicAddYJ = "http://" + HOST + ":" + IISPORT + "/server/WebAnswerGrade.asmx";//添加保存图片(应急指挥)

    //矿产资源
    //http://server.lbschina.com.cn:6081/server/WebMineralProducts.asmx
    public static String KCURL = "http://" + HOST + ":" + IISPORT + "/server/WebMineralProducts.asmx";
    public static String SBYDURL = "http://" + HOST + ":" + IISPORT + "/server/WebLandReported.asmx";
    //决策用地
    //占压分析
    public static String ZYFXURL = "http://" + HOST + ":" + IISPORT + "/server/WebLandDecisionAnalysis.asmx";
    public static String ZYFXURL2 = "http://" + HOST + ":" + IISPORT + "/server/WebNwaLandAnaysis.asmx";
    //批准用地
    public static String PZYDURL = "http://" + HOST + ":" + IISPORT + "/server/WebLandRatify.asmx";
    //供应用地
    public static String GYYDURL = "http://" + HOST + ":" + IISPORT + "/server/WebLandProvision.asmx";
    //储备用地
    public static String CBYDURL = "http://" + HOST + ":" + IISPORT + "/server/WebLandReserve.asmx";
    //巡查任务(包括地质灾害任务、矿产、决策)
    public static String XCRWURL = "http://" + HOST + ":" + IISPORT + "/server/WebMobileInspectionMission.asmx";
    //卫片执法
    public static String WPZFURL = "http://" + HOST + ":" + IISPORT + "/server/WebWeiChipLawEnforcement.asmx";

    //乡镇区划地图服务
    public static String DZZHMAPURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJS_XZQH/MapServer";
    public static String XZQHMAPURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJGT/BJS_DT/MapServer";
    //影像地图服务
    //public static String IMAGEURL="http://"+HOST+":"+MAPPORT+"/arcgis/rest/services/HillShade/ImageServer";

    public static String IMAGEURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJIMAGEDATA/BJImage_Phone/MapServer";

    //土地利用现状图
    public static String TDLYXZURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJSDE/TDLYXZ_2013/MapServer";
    //规划图
    public static String TDGHURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJSDE/TDLYZTGH_2012/MapServer";
    //坝区基本农田图
    public static String TDJBNTURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJSDE/QMDB/MapServer";
    //报批地块图
    public static String BPDKTURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJSDE/BPYD/MapServer";
    //城市规划图
    public static String CSGHTURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJSDE/CSGH/MapServer";
    //矿产分布图
    public static String KCFBTURL = "http://" + HOST + ":" + MAPPORT + "/arcgis/rest/services/BJSDE/MINE/MapServer";

    //数据库名称
    public final static String BJ_DATABASE_FILENAME = "dzzh.db";
    //配置参数数据库
    public final static String BJ_CONF_DATABASE_FILENAME = "confparam.db";
    //数据库路径
    public final static String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BJApp/db";

}
