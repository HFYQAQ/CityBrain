package cn.edu.neu.citybrain.connector.kafka.util;

import cn.edu.neu.citybrain.db.DBConnection;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class Kafka2Mysql {
    private static final String SQL = "insert into inter_metric_v2(inter_id, f_rid, turn_dir_no, dt, step_index, travel_time, delay_dur, stop_cnt, queue_len) values(?,?,?,?,?,?,?,?,?)";
    private static final long BATCH_COUNT = 100000;
    private static final int MAX_CONSUME_TIMES = 5;

    private static void loadDataFromSpeedRT(String servers, String topic) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("group.id", "foo");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "false");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        try (Consumer<String, String> consumer = new KafkaConsumer<>(properties);
             Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            connection.setAutoCommit(false);
            consumer.subscribe(Arrays.asList(topic));

            long counter = 0;
            long times = 0;
            for (;;) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

                if (records.isEmpty()) {
                    times++;
                    if (times >= MAX_CONSUME_TIMES) { // 超过一定次数消费不到数据，判断消费完毕，提交剩余数据后退出
                        preparedStatement.executeBatch();
                        connection.commit();
                        break;
                    }
                }

                for (ConsumerRecord<String, String> record : records) {
                    String line = record.value();
                    String[] vals = line.split(","); // rid,travel_time,speed,reliability_code,step_index,day_of_week,timestamp
                    if (vals.length != 9) {
                        throw new Exception("Kafka consume exception: invalid line.");
                    }
                    String interId = vals[0];
                    String fRid = vals[1];
                    Long turnDirNo = Long.parseLong(vals[2]);
                    String dt = vals[3];
                    Long stepIndex1mi = Long.parseLong(vals[4]);
                    Double travelTime = Double.parseDouble(vals[5]);
                    Double delayDur = Double.parseDouble(vals[6]);
                    Double stopCnt = Double.parseDouble(vals[7]);
                    Double queueLen = Double.parseDouble(vals[8]);

                    preparedStatement.setObject(1, interId);
                    preparedStatement.setObject(2, fRid);
                    preparedStatement.setObject(3, turnDirNo);
                    preparedStatement.setObject(4, dt);
                    preparedStatement.setObject(5, stepIndex1mi);
                    preparedStatement.setObject(6, travelTime);
                    preparedStatement.setObject(7, delayDur);
                    preparedStatement.setObject(8, stopCnt);
                    preparedStatement.setObject(9, queueLen);
                    preparedStatement.addBatch();

                    counter++;
                    if (counter % BATCH_COUNT == 0) { // 成批提交
                        preparedStatement.executeBatch();
                        connection.commit();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // parameters
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        // help
        if (parameterTool.has("h")) {
            System.out.printf("Usage:\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n",
                    "--servers", "kafka servers to connect, default value is \"kafka-service:9092\".",
                    "--topic", "topic which to load, default value is \"inter_metric\".");
            return;
        }
        String servers = parameterTool.get("servers", "kafka-service:9092");
        String topic = parameterTool.get("topic", "inter_metric");

        // load
        loadDataFromSpeedRT(servers, topic);
        System.out.println("complete!");
    }
}
