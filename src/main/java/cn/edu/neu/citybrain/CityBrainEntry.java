package cn.edu.neu.citybrain;

import cn.edu.neu.citybrain.connector.kafka.source.KafkaSpeedRTSourceFunction;
import cn.edu.neu.citybrain.connector.kafka.util.Constants;
import cn.edu.neu.citybrain.dto.fRidSeqTurnDirIndexDTO;
import cn.edu.neu.citybrain.dto.my.RoadMetric;
import cn.edu.neu.citybrain.function.DefaultValueForNdIndexFunction;
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
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n",
                    "--source", "kafka or mysql, mysql source is used to debug, default value is kafka",
                    "--input-topic", "kafka topic which to read, default value is \"mock_speed_rt\".",
                    "--output-topic", "kafka topic which to restore results, default value is \"inter_metric\".",
                    "--servers", "kafka servers to connect, must be specified explicitly for kafka source, default value is \"" + Constants.ASTERIA_KAFKA_SERVER + "\".",
                    "--source-table", "default mock_speed_rt",
                    "--step-index-num", "default 60",
                    "--scale", "scale, default 0",
                    "--sourceDelay", "source delay for stream source, default value is \"" + ConstantUtil.SOURCE_DELAY + "\"(ms).",
                    "--interval", "read interval for mysql, default 10000",
                    "--parallelism", "parallelism, default value is 1.",
                    "--maxParallelism", "maxParallelism, default value is same with parallelism.",
                    "--isExhibition", "whether exhibit performance, default value is false");
            return;
        }
        // source
        String source = parameterTool.get("source") == null ?
                "kafka" :
                parameterTool.get("source");
        // input-topic
        String inputTopic = parameterTool.get("input-topic") == null ?
                "mock_speed_rt" :
                parameterTool.get("input-topic");
        // output-topic
        String outputTopic = parameterTool.get("output-topic") == null ?
                "inter_metric" :
                parameterTool.get("output-topic");
        // servers
        String servers = parameterTool.get("servers") == null ?
                Constants.ASTERIA_KAFKA_SERVER :
                parameterTool.get("servers");
        // source-table
        String sourceTableName = parameterTool.get("source-table") == null ?
                "mock_speed_rt" :
                parameterTool.get("source-table");
        // stepIndexNum
        long stepIndexNum = parameterTool.get("step-index-num") == null ?
                60 :
                Long.parseLong(parameterTool.get("step-index-num"));
        // scale
        int scale = parameterTool.get("scale") == null ?
                0 :
                Integer.parseInt(parameterTool.get("scale"));
        String table1 = scale == 0 ? dws_tfc_state_rid_tpwkd_index_m : dws_tfc_state_rid_tpwkd_index_m + "_" + scale;
        String table2 = scale == 0 ? dws_tfc_state_signinterfridseq_tpwkd_delaydur_m : dws_tfc_state_signinterfridseq_tpwkd_delaydur_m + "_" + scale;
        // delay
        long sourceDelay = parameterTool.get("sourceDelay") == null ?
                ConstantUtil.SOURCE_DELAY :
                Long.parseLong(parameterTool.get("sourceDelay"));
        // interval
        long interval = parameterTool.get("interval") == null ?
                10000 :
                Long.parseLong(parameterTool.get("interval"));
        // parallelism
        int parallelism = parameterTool.get("parallelism") == null ? 1 : Integer.parseInt(parameterTool.get("parallelism"));
        // maxParallelism
        int maxParallelism = parameterTool.get("maxParallelism") == null ? parallelism : Integer.parseInt(parameterTool.get("maxParallelism"));
        // isExhibition
        boolean isExhibition = parameterTool.get("isExhibition") != null && (parameterTool.get("isExhibition").equals("true"));

        System.out.printf("bootstrap parameters:\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n",
                "--source", source,
                "--input-topic", inputTopic,
                "--output-topic", outputTopic,
                "--servers", servers,
                "--source-table", sourceTableName,
                "--step-index-num", stepIndexNum,
                "--scale", scale,
                "--sourceDelay", sourceDelay,
                "--interval", interval,
                "--parallelism", parallelism,
                "--maxParallelism", maxParallelism,
                "--isExhibition", isExhibition);

        // environment
        final MixStreamExecutionEnvironment env = MixStreamContextEnvironment.getExecutionEnvironment()
                .setParallelism(parallelism)
                .setMaxParallelism(maxParallelism);
        env.setMixStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);
        ParameterTool globalParams = ParameterTool.fromArgs(new String[] {"--jobName", ConstantUtil.JOB_NAME});
        env.getConfig().setGlobalJobParameters(globalParams);

        // source
        SourceFunction<Row> sourceFunction = null;
        switch (source) {
            case "kafka":
                sourceFunction = new KafkaSpeedRTSourceFunction(servers, inputTopic, sourceDelay, parallelism, maxParallelism);
                break;
            case "mysql":
                sourceFunction = new SpeedRTSourceFunction(sourceTableName, stepIndexNum, sourceDelay, interval, parallelism, maxParallelism);
                break;
            default:
                sourceFunction = new KafkaSpeedRTSourceFunction(servers, inputTopic, sourceDelay, parallelism, maxParallelism);
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
                .setTableName(table1)
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
                .setTableName(table2)
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
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply()
                .map(new DefaultValueForNdIndexFunction())
                .returns(DefaultValueForNdIndexFunction.getRowTypeInfo());
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
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
//                .slotSharingGroup("xjoin");
        // dws_tfc_state_rid_tpwkd_index_m
        DataMixStream<Row> sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndex = sourceExpandRidInfoExpandInterLaneExpandNdIndexExpandPhasedir
                .xjoinV4(tpwkdIndex)
                .projectStreamTableKey(2, 4, 6)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)
                .ProjectSideTableKey(0, 2, 3)
                .projectsideTableField(0, 1, 2, 3)
                .SyncXjoin()
                .setXjoinType(XjoinType.LEFT)
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();
        // dws_tfc_state_signinterfridseq_tpwkd_delaydur_m
        DataMixStream<Row> sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndexExpDelaydur = sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndex
                .xjoinV4(tpwkdDelaydur)
                .projectStreamTableKey(2, 3, 4, 6)
                .projectStreamTableField(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
                .ProjectSideTableKey(0, 1, 3, 4)
                .projectsideTableField(0, 1, 2, 3, 4)
                .SyncXjoin()
                .setXjoinType(XjoinType.LEFT)
                .enablePartition()
                .setParallelism(parallelism)
                .setCacheTimeout(ConstantUtil.CACHE_TIMEOUT)
                .apply();

        // watermark
        DataMixStream<Row> speedRTWithWatermark = sourceExpRidInfoExpLaneExpNdIndexExpPhaseExpTpwkdIndexExpDelaydur.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.seconds(1)) {
            @Override
            public long extractTimestamp(Row row) {
                return (long) row.getField(7);
            }
        });
        // window
        DataMixStream<RoadMetric> singleIntersectionAnalysisResult = speedRTWithWatermark
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.minutes(1)))
                .process(new SingleIntersectionAnalysisFunction(isExhibition));
        singleIntersectionAnalysisResult.addSink(new KafkaSinkFunction(servers, outputTopic)).setParallelism(1);

        env.execute(ConstantUtil.JOB_NAME);
    }
}
