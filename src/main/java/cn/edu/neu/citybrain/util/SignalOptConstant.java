package cn.edu.neu.citybrain.util;

public class SignalOptConstant {
    ////////////流量算法的参数/////////////
    /**
     * 流量异常值判断时，分段函数的分段点
     */
    public final static String PARAM_FLOW_STAGE_DIVIDING_POINT = "StageDividingPoint";
    /**
     * 流量过大判断时，理论通行能力的放大系数
     */
    public final static String PARAM_FLOW_TC_COE = "TheoryCapacityCoe";

    ////////////////////////////速度相关参数///////////////////////////////////////////
    /**
     * 默认速度
     */
    public final static double DEFAULT_SPEED = 40;
    /**
     * 默认速度的置信度
     */
    public final static double DEFAULT_SPEED_RELIABILITY = 20;

    /////////////////////////////////设备类型编码////////////////////////////////////////
    /**
     * 设备数据源类型  流量
     */
    public final static Long DEVCDATATYPE_flow = 1L;
    /**
     * 设备数据源类型 过车
     */
    public final static Long DEVCDATATYPE_VHC = 2L;

    ////////////////////////////////设备状态////////////////////////////////////////////
    /**
     * 正常
     */
    public final static String DEVCSTATUS_NORMAL = "1";
    /**
     * 故障
     */
    public final static String DEVCSTATUS_ABNORMAL = "0";

    /////////////////////////////////车道类型编码////////////////////////////////////////
    /**
     * 车道类型 进口道
     */
    public final static long FT_TYPE_NO_F = 1;

    /////////////////////////////////车流转向编码////////////////////////////////////////
    /**
     * 车流转向编码 左转
     */
    public final static long TURN_DIR_NO_LEFT = 1;
    /**
     * 车流转向编码 直行
     */
    public final static long TURN_DIR_NO_STRAIGHT = 2;
    /**
     * 车流转向编码 右转
     */
    public final static long TURN_DIR_NO_RIGHT = 3;
    /**
     * 车流转向编码 掉头
     */
    public final static long TURN_DIR_NO_ROUND = 4;
    /**
     * 车流转向编码 其他
     */
    public final static long TURN_DIR_NO_OTHERS = 0;
    /**
     * 车流转向编码 未识别
     */
    public final static long TURN_DIR_NO_UNKNOWN = -1;

    /////////////////////////////////特殊车牌定义////////////////////////////////////////
    /**
     * 行人的车牌
     */
    public static final String VHC_NO_PASSENGER = "888888888";

    /**
     * 非机动车牌号
     */
    public static final String VHC_NO_NON_MOTOR = "999999999";
    /**
     * 未识别出的车牌号
     */
    public static final String VHC_NO_UNIDENTIFIED = "UNKNOWN";
    /**
     * 未识别出的车牌号
     */
    public static final String VHC_NO_UNIDENTIFIED1 = "无车牌";


    /////////////////////////////////数据上下游传递的标签名称////////////////////////////////////////
    /**
     * 设备流量
     */
    public static final String CONTEXT_DEVC_LANE_FLOW = "context_devc_lane_flow";
    /**
     * 实时信号配时数据
     */
    public static final String CONTEXT_SIGNAL_DATA = "context_signal_data";
    /**
     * 设备流量统计参数
     */
    public static final String CONTEXT_DEVC_FLOW_PARAM = "context_devc_flow_param";
    /**
     * 路口信息
     */
    public static final String CONTEXT_INTER_INFO = "signal_inter_ino";
    /**
     * 设备异常值修复流量
     */
    public static final String CONTEXT_DEVC_LANE_REPAIRFLOW = "context_devc_lane_repairflow";
    /**
     * 已知rid转向流量
     */
    public static final String CONTEXT_INTERRId_MULTIFLOW = "context_interrid_multiflow";

    ////////////////////////////////信息点类型////////////////////////////////////
    /**
     * 路网百米桩点
     */
    public static final String IOITYPE_100_METER = "100_meter";

    /**
     * 卡口
     */
    public static final String IOITYPE_GANTRY = "gantry";

    ////////////////////////////////设备类型名称////////////////////////////////////
//
//    /**
//     * 获取设备类型
//     * @param devcTypeNo
//     * @return
//     */
//    public static String getDevcType(long devcTypeNo){
//        if(StaticConstantClass.DEVCTYPENO_BAYONET.equals(devcTypeNo)){
//            return "gantry";
//        }else {
//            return null;
//        }
//    }
    /////////////////////////////////其他////////////////////////////////////////
    /**
     * 空字符串
     */
    public final static String EmptyString = "";
    /**
     * 主键拼接的分隔符
     */
    public final static String Seperator = "_";
    /**
     * 报错消息中多个实体的分隔符
     */
    public final static String ErrorMsg_Delimiter = "|";
    /**
     * 根据设备类型设置设备置信度
     */
    public final static String PARAM_DEVCTYPE_RELIABILITYCODE = "devcReliabilityCode";
    /**
     * 无效的phase_plan_id
     */
    public final static String INVALID_PHASE_PLAN_ID = "-1";
    /**
     * 无效的time_plan_id
     */
    public final static String INVALIE_TIME_PLAN_ID = "-1";
    /**
     * 数值的无效值
     */
    public final static double INVALID_VALUE = -1.0;
    /**
     * 使用历史信号配时填补的信号配时方案的来源
     */
    public final static String SIGNALOPER_SOURCE_TYPE_HISTORY = "History";
    /**
     * 算法的设备ID
     */
    public final static String DEVC_ID_ALGORITHM = "Algorithm";

    /**************************************指标算法的详细描述***************************************************/
    /**
     * 纯高德数据源
     */
    public final static String SOURCE_AMAP = "amap";
    /**
     * 信号配时数据
     */
    public final static String SOURCE_SIGNAL = "signal";
    /**
     * 客户检测器数据
     */
    public final static String SOURCE_DETECTOR = "detector";
    /**
     * 计算模型：统计模型
     */
    public final static String ALG_TYPE_STAT = "statistic";
}
