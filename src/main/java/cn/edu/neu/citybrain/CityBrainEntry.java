package cn.edu.neu.citybrain;

import cn.edu.neu.citybrain.connector.kafka.source.KafkaSpeedRTSourceFunction;
import cn.edu.neu.citybrain.connector.kafka.util.Constants;
import cn.edu.neu.citybrain.dto.my.RoadMetric;
import cn.edu.neu.citybrain.function.SingleIntersectionAnalysisFunction;
import cn.edu.neu.citybrain.function.sink.KafkaSinkFunction;
import cn.edu.neu.citybrain.function.source.SpeedRTSourceFunction;
import cn.edu.neu.citybrain.util.ConstantUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamContextEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;

import java.util.List;

import static cn.edu.neu.citybrain.db.DBConstants.dws_tfc_state_rid_tpwkd_index_m;
import static cn.edu.neu.citybrain.db.DBConstants.dws_tfc_state_signinterfridseq_tpwkd_delaydur_m;

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
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n",
                    "--source", "kafka or mysql, mysql source is used to debug, default value is kafka",
                    "--input-topic", "kafka topic which to read, default value is \"mock_speed_rt\".",
                    "--output-topic", "kafka topic which to restore results, default value is \"inter_metric\".",
                    "--servers", "kafka servers to connect, must be specified explicitly for kafka source, default value is \"" + Constants.ASTERIA_KAFKA_SERVER + "\".",
                    "--source-table", "default mock_speed_rt",
                    "--step-index-num", "default 60",
                    "--scale", "scale",
                    "--sourceDelay", "source delay for stream source, default value is \"" + ConstantUtil.SOURCE_DELAY + "\"(ms).",
                    "--interval", "read interval for mysql, default 10000",
                    "--parallelism", "parallelism, default value is 1.",
                    "--maxParallelism", "maxParallelism, default value is same with parallelism.",
                    "--flinkVersion", "flink version which is used to define job name, default value is \"\".",
                    "--isExhibition", "whether exhibit performance, default value is false");
            return;
        }
        // source
        String source = parameterTool.get("source") == null ?
                "kafka" :
                parameterTool.get("source");
        // output-topic
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
        // flinkVersion
        String flinkVersion = parameterTool.get("flinkVersion") == null ? "" : parameterTool.get("flinkVersion");
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
                "--flinkVersion", flinkVersion,
                "--isExhibition", isExhibition);

        // environment
        final StreamExecutionEnvironment env = StreamContextEnvironment.getExecutionEnvironment()
                .setParallelism(parallelism)
                .setMaxParallelism(maxParallelism);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(1000);
        String jobName = ConstantUtil.JOB_NAME_PREFIX + flinkVersion + "-" + System.currentTimeMillis();
        ParameterTool globalParams = ParameterTool.fromArgs(new String[] {"--jobName", jobName});
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
        DataStream<Row> speedRTSource = env
                .addSource(sourceFunction).setParallelism(1)
                .name("speedRT")
                .returns(KafkaSpeedRTSourceFunction.getRowTypeInfo());

        // watermark
        DataStream<Row> speedRTWithWatermark = speedRTSource.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<Row>(Time.seconds(0)) {
            @Override
            public long extractTimestamp(Row row) {
                return (long) row.getField(8);
            }
        });
        // window
        DataStream<List<RoadMetric>> singleIntersectionAnalysisResult = speedRTWithWatermark
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.minutes(1)))
                .process(new SingleIntersectionAnalysisFunction(table1, table2, isExhibition));
//        singleIntersectionAnalysisResult.writeAsText("/opt/flink/citybrain.out", OVERWRITE);
        singleIntersectionAnalysisResult.addSink(new KafkaSinkFunction(servers, outputTopic)).setParallelism(1);
//        singleIntersectionAnalysisResult.addSink(new MySQLSinkFunction()).setParallelism(1);
//        singleIntersectionAnalysisResult.addSink(new MetricSinkFunction()).setParallelism(1);

        env.execute(jobName);
    }
}
