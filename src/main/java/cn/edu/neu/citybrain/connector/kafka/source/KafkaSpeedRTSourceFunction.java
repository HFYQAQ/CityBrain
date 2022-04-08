package cn.edu.neu.citybrain.connector.kafka.source;

import cn.edu.neu.citybrain.connector.kafka.util.Constants;
import cn.edu.neu.citybrain.util.CityBrainUtil;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.mix.api.functions.source.RichSourceFunction;
import org.apache.flink.runtime.state.KeyGroupRangeAssignment;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaSpeedRTSourceFunction extends RichSourceFunction<Row> {
    private Consumer<String, String> consumer;
    private String servers;
    private String topic;
    private long sourceDelay;
    private int parallelism;
    private int maxParallelism;
    private static int autoInc; // 对parallelism取模为每条数据打上key，实现rebalance，保证同一个子任务上的数据共用同一个key
    private int[] preallocatedKeys;

    // metrics
    private long loop;
    private long numRecord;
    private int[] assignedKeysDistribution;

    public KafkaSpeedRTSourceFunction(String servers, String topic, long sourceDelay, int parallelism, int maxParallelism) {
        this.servers = servers;
        this.topic = topic;
        this.sourceDelay = sourceDelay;
        this.parallelism = parallelism;
        this.maxParallelism = maxParallelism;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        preallocatedKeys = CityBrainUtil.generateKeyTagForPartition(parallelism, maxParallelism);
        assignedKeysDistribution = new int[preallocatedKeys.length];

        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("group.id", "foo");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "false");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(properties);
    }

    @Override
    public void run(SourceContext<Row> sourceContext) throws Exception {
        if (consumer == null) {
            throw new Exception("Kafka consume exception: consumer is null.");
        }

        System.out.printf("waiting for side table loaded with sourceDelay %d.\n", sourceDelay);
        int sleepCnt = (int) sourceDelay / 5000;
        for (int i = 0; i < sleepCnt; i++) {
            Thread.sleep(5 * 1000);
            System.out.printf("sleeping %ds.\n", (i + 1) * 5);
        }
        System.out.println("unblock stream");

        consumer.subscribe(Arrays.asList(topic));
        for (;;) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(3000));

            loop++;
            for (ConsumerRecord<String, String> record : records) {
                String line = record.value();
                String[] vals = line.split(","); // rid,travel_time,speed,reliability_code,step_index,day_of_week,timestamp
                if (vals.length != 7) {
                    throw new Exception("Kafka consume exception: invalid line.");
                }
                String rid = vals[0];
                Double travelTime = Double.parseDouble(vals[1]);
                Double speed = Double.parseDouble(vals[2]);
                Double reliabilityCode = Double.parseDouble(vals[3]);
                long stepIndex1mi = Long.parseLong(vals[4]);
                Long stepIndex10mi = stepIndex1mi / 10;
                Long dayOfWeek = Long.parseLong(vals[5]);
                Long timestamp = Long.parseLong(vals[6]);

                Row row = new Row(9);
                autoInc = autoInc == Integer.MAX_VALUE ? 0 : autoInc;
                row.setField(0, preallocatedKeys[autoInc++ % parallelism]); // attach tag as partition key
                row.setField(1, rid);
                row.setField(2, travelTime);
                row.setField(3, speed);
                row.setField(4, reliabilityCode);
                row.setField(5, stepIndex1mi);
                row.setField(6, stepIndex10mi); // convert from 1mi to 10mi
                row.setField(7, dayOfWeek);
                row.setField(8, timestamp); // 以1mi的时间片长度计算
                sourceContext.collect(row);

                assignedKeysDistribution[KeyGroupRangeAssignment.assignKeyToParallelOperator(row.getField(0), maxParallelism, parallelism)]++;
                numRecord++;
            }
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void close() throws Exception {
        super.close();

        System.out.printf("loop cnt: %d, num of records: %d\n", loop, numRecord);
        StringBuilder sb = new StringBuilder("assigned keys distribution: ");
        for (int i = 0; i < assignedKeysDistribution.length; i++) {
            sb.append("[").append(i + 1).append("]")
                    .append(assignedKeysDistribution[i]).append(" ");
        }
        System.out.println(sb);

        if (consumer != null) {
            consumer.close();
        }
    }

    public static RowTypeInfo getRowTypeInfo() {
        return new RowTypeInfo(
                TypeInformation.of(Integer.TYPE),
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE));
    }
}
