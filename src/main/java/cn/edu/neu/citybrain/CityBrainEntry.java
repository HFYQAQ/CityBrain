package cn.edu.neu.citybrain;

import cn.edu.neu.citybrain.connector.kafka.source.KafkaSpeedRTSourceFunction;
import cn.edu.neu.citybrain.connector.kafka.util.Constants;
import cn.edu.neu.citybrain.dto.fRidSeqTurnDirIndexDTO;
import cn.edu.neu.citybrain.function.InterLaneScatterFunction;
import cn.edu.neu.citybrain.function.SingleIntersectionAnalysisFunction;
import cn.edu.neu.citybrain.function.sink.KafkaSinkFunction;
import cn.edu.neu.citybrain.function.source.SpeedRTSourceFunction;
import cn.edu.neu.citybrain.util.ConstantUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.mix.api.TimeCharacteristic;
import org.apache.flink.mix.api.datastream.DataMixStream;
import org.apache.flink.mix.api.environment.MixStreamContextEnvironment;
import org.apache.flink.mix.api.environment.MixStreamExecutionEnvironment;
import org.apache.flink.mix.api.functions.source.SourceFunction;
import org.apache.flink.mix.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.mix.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.mix.api.windowing.time.Time;
import org.apache.flink.mix.api.xjoin2.client.rdb.table.RdbSideTableInfo;
import org.apache.flink.mix.api.xjoin2.client.rdb.table.RdbSideTableInfoBuilders;
import org.apache.flink.mix.api.xjoin2.core.enums.XjoinType;
import org.apache.flink.types.Row;

import static cn.edu.neu.citybrain.db.DBConstants.*;

public class CityBrainEntry {
    public static void main(String[] args) throws Exception {
        // parameters
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        // help
        if (parameterTool.has("h")) {
            System.out.printf("Usage:\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n",
                    "--source", "kafka or mysql, mysql source is used to debug, default value is kafka",
                    "--servers", "kafka servers to connect, must be specified explicitly for kafka source, default value is \"" + Constants.ASTERIA_KAFKA_SERVER + "\".",
                    "--sourceDelay", "source delay for stream source, default value is \"" + ConstantUtil.SOURCE_DELAY + "\"(ms).",
                    "--parallelism", "parallelism, default value is 1.",
                    "--maxParallelism", "maxParallelism, default value is same with parallelism.");
            return;
        }
        // source
        String source = parameterTool.get("source") == null ?
                "kafka" :
                parameterTool.get("source");
        // servers
        String servers = parameterTool.get("servers") == null ?
                Constants.ASTERIA_KAFKA_SERVER :
                parameterTool.get("servers");
        // delay
        long sourceDelay = parameterTool.get("sourceDelay") == null ?
                ConstantUtil.SOURCE_DELAY :
                Long.parseLong(parameterTool.get("sourceDelay"));
        // parallelism
        int parallelism = parameterTool.get("parallelism") == null ? 1 : Integer.parseInt(parameterTool.get("parallelism"));
        // maxParallelism
        int maxParallelism = parameterTool.get("maxParallelism") == null ? parallelism : Integer.parseInt(parameterTool.get("maxParallelism"));

        System.out.printf("bootstrap parameters:\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n",
                "--source", source,
                "--servers", servers,
                "--sourceDelay", sourceDelay,
                "--parallelism", parallelism,
                "--maxParallelism", maxParallelism);

        // environment
        final MixStreamExecutionEnvironment env = MixStreamContextEnvironment.getExecutionEnvironment()
                .setParallelism(parallelism)
                .setMaxParallelism(maxParallelism);
        env.setMixStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);

        // source
        SourceFunction<Row> sourceFunction = null;
        switch (source) {
            case "kafka":
                sourceFunction = new KafkaSpeedRTSourceFunction(servers, sourceDelay, parallelism, maxParallelism);
                break;
            case "mysql":
                sourceFunction = new SpeedRTSourceFunction(sourceDelay, parallelism, maxParallelism);
                break;
            default:
                sourceFunction = new KafkaSpeedRTSourceFunction(servers, sourceDelay, parallelism, maxParallelism);
                break;
        }
        DataMixStream<Row> speedRTSource = env
                .addSource(sourceFunction)
                .name("speedRT")
                .returns(KafkaSpeedRTSourceFunction.getRowTypeInfo());

        // dwd_tfc_bas_rdnet_rid_info
        RdbSideTableInfo ridInfo = RdbSideTableInfoBuilders.buildMysqlTableInfo()
                .setUrl(JDBC_URL)
                .setUserName(JDBC_USER)
                .setPassword(JDBC_PWD)
                .setTableName(dwd_tfc_bas_rdnet_rid_info)
                .addfieldInfo("rid", "string")
                .addfieldInfo("len", "string")
                .finish();
        // dwd_tfc_rltn_wide_inter_lane
        RdbSideTableInfo interLane = RdbSideTableInfoBuilders.buildMysqlTableInfo()
                .setUrl(JDBC_URL)
                .setUserName(JDBC_USER)
                .setPassword(JDBC_PWD)
                .setTableName(dwd_tfc_rltn_wide_inter_lane)
                .addfieldInfo("inter_id", "string")
                .addfieldInfo("rid", "string")
                .addfieldInfo("turn_dir_no_list", "string")
                .addfieldInfo("lane_id", "string")
                .finish();
        // dws_tfc_state_signinterfridseq_nd_index_m
        RdbSideTableInfo ndIndex = RdbSideTableInfoBuilders.buildMysqlTableInfo()
                .setUrl(JDBC_URL)
                .setUserName(JDBC_USER)
                .setPassword(JDBC_PWD)
                .setTableName(dws_tfc_state_signinterfridseq_nd_index_m)
                .addfieldInfo("inter_id", "string")
                .addfieldInfo("f_rid", "string")
                .addfieldInfo("turn_dir_no", "string")
                .addfieldInfo("f_ridseq", "string")
                .addfieldInfo("benchmark_nostop_travel_time_3m", "string")
                .finish();
        // dwd_tfc_ctl_signal_phasedir
        RdbSideTableInfo phasedir = RdbSideTableInfoBuilders.buildMysqlTableInfo()
                .setUrl(JDBC_URL)
                .setUserName(JDBC_USER)
                .setPassword(JDBC_PWD)
                .setTableName(dwd_tfc_ctl_signal_phasedir)
                .addfieldInfo("inter_id", "string")
                .addfieldInfo("f_rid", "string")
                .addfieldInfo("turn_dir_no", "string")
                .addfieldInfo("phase_plan_id", "string")
                .addfieldInfo("phase_name", "string")
                .finish();
        // dws_tfc_state_rid_tpwkd_index_m
        RdbSideTableInfo tpwkdIndex = RdbSideTableInfoBuilders.buildMysqlTableInfo()
                .setUrl(JDBC_URL)
                .setUserName(JDBC_USER)
                .setPassword(JDBC_PWD)
                .setTableName(dws_tfc_state_rid_tpwkd_index_m)
                .addfieldInfo("rid", "string")
                .addfieldInfo("avg_travel_time_3m", "string")
                .addfieldInfo("day_of_week", "string")
                .addfieldInfo("step_index", "string")
                .finish();
        // dws_tfc_state_signinterfridseq_tpwkd_delaydur_m
        RdbSideTableInfo tpwkdDelaydur = RdbSideTableInfoBuilders.buildMysqlTableInfo()
                .setUrl(JDBC_URL)
                .setUserName(JDBC_USER)
                .setPassword(JDBC_PWD)
                .setTableName(dws_tfc_state_signinterfridseq_tpwkd_delaydur_m)
                .addfieldInfo("f_rid", "string")
                .addfieldInfo("turn_dir_no", "string")
                .addfieldInfo("avg_trace_travel_time_3m", "string")
                .addfieldInfo("day_of_week", "string")
                .addfieldInfo("step_index", "string")
                .finish();

        // xjoin
        // ridInfo
        DataMixStream<Row> sourceExpandRidInfo = speedRTSource
                .xjoinV4(ridInfo)
                .projectStreamTableKey(1)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8)
                .ProjectSideTableKey(0)
                .projectsideTableField(0, 1)
                .SyncXjoin()
//                .setShareSlot("xjoin")
                .setXjoinType(XjoinType.LEFT)
                .enableIncLog()
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
        // lane
        DataMixStream<Row> sourceExpandRidInfoExpandLane = sourceExpandRidInfo
                .xjoinV4(interLane)
                .projectStreamTableKey(1)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .ProjectSideTableKey(1)
                .projectsideTableField(0, 1, 2, 3)
                .SyncXjoin()
//                .setShareSlot("xjoin")
                .setXjoinType(XjoinType.INNER)
                .enableIncLog()
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
        DataMixStream<Row> sourceExpandRidInfoExpandInterLaneScattered = sourceExpandRidInfoExpandLane
                .flatMap(new InterLaneScatterFunction())
//                .slotSharingGroup("xjoin")
                .returns(InterLaneScatterFunction.getRowTypeInfo());
        // ndIndex
        DataMixStream<Row> sourceExpandRidInfoExpandInterLaneExpandNdIndex = sourceExpandRidInfoExpandInterLaneScattered
                .xjoinV4(ndIndex)
                .projectStreamTableKey(1, 2, 3)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
                .ProjectSideTableKey(0, 1, 2)
                .projectsideTableField(0, 1, 2, 3, 4)
                .SyncXjoin()
//                .setShareSlot("xjoin")
                .setXjoinType(XjoinType.LEFT)
                .enableIncLog()
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
//                .slotSharingGroup("xjoin");
        // phasedir
        DataMixStream<Row> sourceExpandRidInfoExpandInterLaneExpandNdIndexExpandPhasedir = sourceExpandRidInfoExpandInterLaneExpandNdIndex
                .xjoinV4(phasedir)
                .projectStreamTableKey(1, 2, 3)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
                .ProjectSideTableKey(0, 1, 2)
                .projectsideTableField(0, 1, 2, 3, 4)
                .SyncXjoin()
//                .setShareSlot("xjoin")
                .setXjoinType(XjoinType.LEFT)
                .enableIncLog()
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
//                .slotSharingGroup("xjoin");
        // dws_tfc_state_rid_tpwkd_index_m
        DataMixStream<Row> sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndex = sourceExpandRidInfoExpandInterLaneExpandNdIndexExpandPhasedir
                .xjoinV4(tpwkdIndex)
                .projectStreamTableKey(2, 5, 6)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
                .ProjectSideTableKey(0, 2, 3)
                .projectsideTableField(0, 1, 2, 3)
                .SyncXjoin()
                .setXjoinType(XjoinType.LEFT)
                .enableIncLog()
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
        // dws_tfc_state_signinterfridseq_tpwkd_delaydur_m
        DataMixStream<Row> sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndexExpDelaydur = sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndex
                .xjoinV4(tpwkdDelaydur)
                .projectStreamTableKey(2, 3, 5, 6)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
                .ProjectSideTableKey(0, 1, 3, 4)
                .projectsideTableField(0, 1, 2, 3, 4)
                .SyncXjoin()
                .setXjoinType(XjoinType.LEFT)
                .enableIncLog()
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();

        // watermark
        DataMixStream<Row> speedRTWithWatermark = sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndexExpDelaydur.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.seconds(0)) {
            @Override
            public long extractTimestamp(Row row) {
                return (long) row.getField(7);
            }
        });
        // window
        DataMixStream<fRidSeqTurnDirIndexDTO> singleIntersectionAnalysisResult = speedRTWithWatermark
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.minutes(1)))
                .process(new SingleIntersectionAnalysisFunction());
//        singleIntersectionAnalysisResult.writeAsText("/opt/flink/citybrain.out", OVERWRITE);
        singleIntersectionAnalysisResult.addSink(new KafkaSinkFunction(servers));

        env.execute("CityBrainJob");
    }
}
