package cn.edu.neu.citybrain.connector.kafka.util;

import cn.edu.neu.citybrain.db.DBConstants;

public class Constants {
    // servers
    public static final String NEU_KAFKA_SERVER = "master:9092,s1:9092,s2:9092";
    public static final String ASTERIA_KAFKA_SERVER = "kafka-service:9092";
    public static final String NEU_ZOOKEEPER_SERVER = "master:2181,s1:2181,s2:2181";

    // topics
    public static final String TOPIC_DWS_TFC_STATE_RID_TP_LASTSPEED_RT = DBConstants.dws_tfc_state_rid_tp_lastspeed_rt;
//    public static final String TOPIC_DWD_TFC_CTL_INTERSIGNAL_OPER_RT = DBConstants.dwd_tfc_ctl_intersignal_oper_rt;
}
