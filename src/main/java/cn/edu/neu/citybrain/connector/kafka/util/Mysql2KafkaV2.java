package cn.edu.neu.citybrain.connector.kafka.util;

import cn.edu.neu.citybrain.db.DBConnection;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class Mysql2KafkaV2 {
    private static final String SPEED_RT_SOURCE = "select rid, travel_time, speed, reliability_code, step_index, WEEKDAY(dt) as day_of_week, UNIX_TIMESTAMP(dt) as timestamp from %s where step_index=?;";

    private static void loadDataFromSpeedRT(String servers, String tableName, int from, int to) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("linger.ms", 1);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        try (Producer<String, String> producer = new KafkaProducer<>(properties);
             Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(String.format(SPEED_RT_SOURCE, tableName))) {
            for (int stepIndex = from; stepIndex <= to; stepIndex++) {
                long start = System.currentTimeMillis();
                System.out.printf("producing lastspeed_rt records with step_index %d...\n", stepIndex);

                preparedStatement.clearParameters();
                preparedStatement.setObject(1, stepIndex);
                ResultSet resultSet = preparedStatement.executeQuery();
                int cnt = 0;
                while (resultSet.next()) {
                    Row row = new Row(7);
                    row.setField(0, resultSet.getString("rid"));
                    row.setField(1, resultSet.getDouble("travel_time"));
                    row.setField(2, resultSet.getDouble("speed"));
                    row.setField(3, resultSet.getDouble("reliability_code"));
                    row.setField(4, stepIndex);
                    row.setField(5, resultSet.getInt("day_of_week"));
                    row.setField(6, resultSet.getLong("timestamp") * 1000 + (long) stepIndex * 60 * 1000);
                    producer.send(new ProducerRecord<>(Constants.TOPIC_DWS_TFC_STATE_RID_TP_LASTSPEED_RT, row.toString()));

                    cnt++;
                }

                System.out.printf("completed! contains %d records.\n", cnt);
//                while (System.currentTimeMillis() - start < 60 * 1000) {
//                    Thread.sleep(3 * 1000);
//                }
            }
            System.out.println("no more records can read from mysql.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // parameters
        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        // help
        if (parameterTool.has("h")) {
            System.out.printf("Usage:\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n",
                    "--servers", "kafka servers to connect, default value is \"kafka-service:9092\".",
                    "--tableName", "table which to load, default value is \"table2\".",
                    "--from", "lower for step_index, default value is 0.",
                    "--to", "upper from step_index, default value is 1");
            return;
        }
        String servers = parameterTool.get("servers", "kafka-service:9092");
        String tableName = parameterTool.get("tableName", "table2");
        int from = parameterTool.get("from") == null ? 0 : Integer.parseInt(parameterTool.get("from"));
        int to = parameterTool.get("to") == null ? 1 : Integer.parseInt(parameterTool.get("to"));

        // load
        loadDataFromSpeedRT(servers, tableName, from, to);
        System.out.println("complete!");
    }
}
