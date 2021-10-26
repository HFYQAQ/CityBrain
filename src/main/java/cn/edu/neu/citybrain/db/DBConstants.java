package cn.edu.neu.citybrain.db;

public class DBConstants {
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static final String REMOTE_IP_PORT = "mysql:3306";
    public static final String JDBC_URL = "jdbc:mysql://" + REMOTE_IP_PORT + "/city_brain?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";
    public static final String JDBC_USER = "root";
    public static final String JDBC_PWD = "123456";




    /**
     * table
     */
    // ======================
    //          kafka
    // ======================
    public static final String dws_tfc_state_rid_tp_lastspeed_rt = "dws_tfc_state_rid_tp_lastspeed_rt"; // 实时表
    // ======================
    //          mysql
    // ======================
    // xjoin隐式查询
    public static final String dwd_tfc_bas_rdnet_rid_info = "dwd_tfc_bas_rdnet_rid_info";
    public static final String dwd_tfc_rltn_wide_inter_lane  = "dwd_tfc_rltn_wide_inter_lane";
    public static final String dws_tfc_state_signinterfridseq_nd_index_m = "dws_tfc_state_signinterfridseq_nd_index_m";
    public static final String dwd_tfc_ctl_signal_phasedir  = "dwd_tfc_ctl_signal_phasedir";
    // 显式查询
    public static final String dwd_tfc_ctl_intersignal_oper_rt  = "dwd_tfc_ctl_intersignal_oper_rt";
    public static final String dws_tfc_trl_interfridlane_tp_smtmultiflow_rt  = "dws_tfc_trl_interfridlane_tp_smtmultiflow_rt";
    // 大表
    public static final String dws_tfc_state_rid_tpwkd_index_m  = "dws_tfc_state_rid_tpwkd_index_m";
    public static final String dws_tfc_state_signinterfridseq_tpwkd_delaydur_m  = "dws_tfc_state_signinterfridseq_tpwkd_delaydur_m";
    // 没用到
    public static final String dws_tfc_state_rid_nd_index_m = "dws_tfc_state_rid_nd_index_m";

    /**
     * sql
     */
    // mysql source
    public static final String SPEED_RT_SOURCE = "select rid, travel_time, speed, reliability_code, step_index, WEEKDAY(dt) as day_of_week, UNIX_TIMESTAMP(dt) as timestamp from " + dws_tfc_state_rid_tp_lastspeed_rt + ";";
//    public static final String SPEED_RT_SOURCE = "select rid, travel_time, speed, reliability_code, step_index, WEEKDAY(dt) as day_of_week, UNIX_TIMESTAMP(dt) as timestamp from " + dws_tfc_state_rid_tp_lastspeed_rt + " where dt=? and tp=? and step_index=? and rid='1527G09HH901527N09HIH00';";

    // ======================
    //          显式查询
    // ======================
    public static final String sql_dwd_tfc_ctl_intersignal_oper_rt =
            String.format("select a.inter_id as interId, a.phase_plan_id as phasePlanId, a.phase_name as phaseName, a.cycle_start_time as cycleStartTime, a.split_time as splitTime, a.cycle_time as cycleTime, a.green_time as greenTime " +
                            "from %s a, (select inter_id, max(cycle_start_time) cycle_start_time from %s where UNIX_TIMESTAMP(cycle_start_time)*1000<=? group by inter_id) b " +
                            "where a.inter_id=b.inter_id and a.cycle_start_time=b.cycle_start_time;",
                    dwd_tfc_ctl_intersignal_oper_rt,
                    dwd_tfc_ctl_intersignal_oper_rt);
    public static final String sql_dws_tfc_trl_interfridlane_tp_smtmultiflow_rt =
            String.format("select inter_id as interId, rid, lane_id as laneId, flow, reliability_code as reliabilityCode " +
                            "from %s where " +
                            "tp='%s' and " +
                            "step_index=?;",
                    dws_tfc_trl_interfridlane_tp_smtmultiflow_rt,
                    "5mi");
    public static final String sql_dws_tfc_state_rid_tpwkd_index_m =
            String.format("select rid, avg_travel_time_3m as travelTime, step_index as stepIndex " +
                            "from %s where " +
                            "tp='%s' and " +
                            "day_of_week=?;",
                    dws_tfc_state_rid_tpwkd_index_m,
                    "10mi");
    public static final String sql_dws_tfc_state_signinterfridseq_tpwkd_delaydur_m =
            String.format("select inter_id as interId, f_rid as fRid, turn_dir_no as turnDirNo, avg_trace_travel_time_3m as avgTraceTravelTime, step_index as stepIndex " +
                            "from %s where " +
                            "tp='%s' and " +
                            "day_of_week=?;",
                    dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                    "10mi");


    //-------------base table------------------------

    public static final String sql_dwd_tfc_bas_rdnet_rid_info =
            String.format("select rid, len as length " +
                            "from %s",
                    dwd_tfc_bas_rdnet_rid_info);
    public static final String sql_dwd_tfc_rltn_wide_inter_lane =
            String.format("select inter_id as interId, rid, turn_dir_no_list as turnDirNoList, lane_id as laneId " +
                            "from %s",
                    dwd_tfc_rltn_wide_inter_lane);
    public static final String sql_dws_tfc_state_signinterfridseq_nd_index_m =
            String.format("select inter_id as interId, f_rid as fRid, turn_dir_no as turnDirNo, f_ridseq as fRidseq, benchmark_nostop_travel_time_3m as benchmarkNostopTravelTime " +
                            "from %s",
                    dws_tfc_state_signinterfridseq_nd_index_m);
    public static final String sql_dwd_tfc_ctl_signal_phasedir =
            String.format("select inter_id as interId, f_rid as fRid, turn_dir_no as turnDirNo, phase_plan_id as phasePlanId, phase_name as phaseName " +
                            "from %s",
                    dwd_tfc_ctl_signal_phasedir);

    //-------------没用到------------------------

    public static final String OPER_RT_SOURCE =
            String.format("select inter_id as interId, phase_plan_id as phasePlanId, cycle_start_time as cycleStartTime, phase_name as phaseName, split_time as splitTime, cycle_time as cycleTime, green_time as greenTime " +
                            "from %s where " +
                            "time_plan_id = %d ",
                    dwd_tfc_ctl_intersignal_oper_rt,
                    1);
    // 指标1——静态数据
    public static final String sql_dws_tfc_state_signinterfridseq_tpwkd_delaydur_m_indicator1 =
            String.format("select f_rid as fRid, turn_dir_no as turnDirNo, f_ridseq as fRidseq " +
                            "from %s where " +
                            "tp='%s' and " +
                            "day_of_week=%d and " +
                            "step_index=%d;",
                    dws_tfc_state_signinterfridseq_tpwkd_delaydur_m,
                    "10mi",
                    0,
                    0);
    // 指标2---默认值获取
    public static final String sql_dws_tfc_state_rid_nd_index_m =
            String.format("select rid, benchmark_travel_time_3m as benchmarkTravelTime, benchmark_nostop_travel_time_3m as benchmarkNostopTravelTime " +
                            "from %s;"
                    , dws_tfc_state_rid_nd_index_m);
}