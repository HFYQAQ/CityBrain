package cn.edu.neu.citybrain.connector.kafka.util;

import cn.edu.neu.citybrain.db.DBConnection;
import cn.edu.neu.citybrain.db.DBConstants;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

@Deprecated
public class Mysql2Kafka {
    private static final String SPEED_RT_SOURCE = "select rid, travel_time, speed, reliability_code, step_index, WEEKDAY(dt) as day_of_week, UNIX_TIMESTAMP(dt) as timestamp from %s;";

    private static void loadDataFromSpeedRT(String servers, String tableName, int from, int to) { // 89735条数据，只有883一个时间片
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
            System.out.printf("load from %s with step_index within [%d, %d].\n", DBConstants.dws_tfc_state_rid_tp_lastspeed_rt, from, to);
            for (int stepIndex = from; stepIndex <= to; stepIndex++) {
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

                System.out.printf("importing step_index %d, contains %d records.\n", stepIndex, cnt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadDataFromOperRT(int from, int to) {
//        Properties properties = new Properties();
//        properties.put("bootstrap.servers", Constants.NEU_KAFKA_SERVER);
//        properties.put("acks", "all");
//        properties.put("retries", 0);
//        properties.put("linger.ms", 1);
//        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
//
//        try (Producer<String, String> producer = new KafkaProducer<>(properties);
//             Connection connection = DBConnection.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(DBConstants.OPER_RT_SOURCE)) {
//            System.out.printf("load from %s with step_index within [%d, %d].\n", DBConstants.dwd_tfc_ctl_intersignal_oper_rt, from, to);
//            for (int stepIndex = from; stepIndex <= to; stepIndex++) {
//                ResultSet resultSet = preparedStatement.executeQuery();
//                int cnt = 0;
//                while (resultSet.next()) {
//                    Row row = new Row(7);
//                    row.setField(0, resultSet.getString("inter_id"));
//                    row.setField(1, resultSet.getDouble("phase_plan_id"));
//                    row.setField(2, resultSet.getDouble("cycle_start_time"));
//                    row.setField(3, resultSet.getDouble("phase_name"));
//                    row.setField(3, resultSet.getDouble("split_time"));
//                    row.setField(3, resultSet.getDouble("cycle_time"));
//                    row.setField(3, resultSet.getDouble("green_time"));
//                    producer.send(new ProducerRecord<>(Constants.TOPIC_DWD_TFC_CTL_INTERSIGNAL_OPER_RT, row.toString()));
//                    cnt++;
//                }
//
//                System.out.printf("importing step_index %d, contains %d records.\n", stepIndex, cnt);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
                    "--servers", "kafka servers to connect.",
                    "--tableName", "table which to load, must be specified explicitly.",
                    "--from", "lower for step_index, default value is 0.",
                    "--to", "upper from step_index, default value is 1");
            return;
        }
        if (!parameterTool.has("servers")) {
            throw new Exception("kafka servers which to connect must be specified explicitly!");
        }
        if (!parameterTool.has("tableName")) {
            throw new Exception("table which to load must be specified explicitly!");
        }
        String servers = parameterTool.get("servers");
        String tableName = parameterTool.get("tableName");
        int from = parameterTool.get("from") == null ? 0 : Integer.parseInt(parameterTool.get("from"));
        int to = parameterTool.get("to") == null ? 1 : Integer.parseInt(parameterTool.get("to"));

        // load
        loadDataFromSpeedRT(servers, tableName, from, to);
        System.out.println("complete!");
    }
}
