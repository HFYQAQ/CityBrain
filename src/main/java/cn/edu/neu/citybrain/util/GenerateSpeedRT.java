package cn.edu.neu.citybrain.util;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class GenerateSpeedRT {
    private static final Random random = new Random();

    private static final String INITIAL_DT = "20190327";
    private static final long DAY = 1;

    public static final String JDBC_URL = "jdbc:mysql://mysql-svc:3306/city_brain_hz?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";

    private static void loadDataFromSpeedRT(String servers, String topic, String initialDt, long day, int from, int to) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("acks", "0");
        properties.put("retries", 0);
        properties.put("batch.size", 1048576);
        properties.put("buffer.memory", 67108864);
        properties.put("linger.ms", 1000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        int speedL = Integer.MAX_VALUE;
        int speedU = Integer.MIN_VALUE;
        int travelTimeL = Integer.MAX_VALUE;
        int travelTimeU = Integer.MIN_VALUE;
        int reliabilityCodeL = Integer.MAX_VALUE;
        int reliabilityCodeU = Integer.MIN_VALUE;

        // read
//        Row template = new Row(15); boolean set = false;
        try (Connection connection = DriverManager.getConnection(JDBC_URL, "root", "123456");
             PreparedStatement preparedStatement = connection.prepareStatement("select * from dws_tfc_state_rid_tp_lastspeed_rt")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String statTime = resultSet.getString("stat_time");
                String rid = resultSet.getString("rid");
                Long stepIndex = resultSet.getLong("step_index");
                Long dataStepIndex = resultSet.getLong("data_step_index");
                String dataTp = resultSet.getString("data_tp");
                String dataStepTime = resultSet.getString("data_step_time");
                Double speed = resultSet.getDouble("speed");
                Double nostopSpeed = resultSet.getDouble("nostop_speed");
                Double travelTime = resultSet.getDouble("travel_time");
                Double nostopTravelTime = resultSet.getDouble("nostop_travel_time");
                Double reliabilityCode = resultSet.getDouble("reliability_code");
                String dt = resultSet.getString("dt");
                String tp = resultSet.getString("tp");
                String dataVersion = resultSet.getString("data_version");
                String adcode = resultSet.getString("adcode");

                speedL = Math.min(speedL, (int) Math.floor(speed));
                speedU = Math.max(speedL, (int) Math.ceil(speed));
                travelTimeL = Math.min(travelTimeL, (int) Math.floor(travelTime));
                travelTimeU = Math.max(travelTimeU, (int) Math.ceil(travelTime));
                reliabilityCodeL = Math.min(reliabilityCodeL, (int) Math.floor(reliabilityCode));
                reliabilityCodeU = Math.max(reliabilityCodeU, (int) Math.ceil(reliabilityCode));

//                if (!set) {
//                    template.setField(0, statTime);
//                    template.setField(1, rid);
//                    template.setField(2, stepIndex);
//                    template.setField(3, dataStepIndex);
//                    template.setField(4, dataTp);
//                    template.setField(5, dataStepTime);
//                    template.setField(6, speed);
//                    template.setField(7, nostopSpeed);
//                    template.setField(8, travelTime);
//                    template.setField(9, nostopTravelTime);
//                    template.setField(10, reliabilityCode);
//                    template.setField(11, dt);
////                template.setField(12, tp);
//                    template.setField(12, "1mi");
//                    template.setField(13, dataVersion);
//                    template.setField(14, adcode);
//
//                    set = true;
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ridSet
        Set<String> ridSet = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, "root", "123456");
             PreparedStatement ps = connection.prepareStatement("select rid from dwd_tfc_rltn_wide_inter_lane")) {
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                ridSet.add(resultSet.getString("rid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write
        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            Date dtFrom = new SimpleDateFormat("yyyyMMdd").parse(initialDt);
            Date dtTo = new Date(dtFrom.getTime() + (day - 1) * 24 * 60 * 60 * 1000);
            for (Date date = dtFrom; date.compareTo(dtTo) <= 0; date = new Date(date.getTime() + 24 * 60 * 60 * 1000)) {
                String curDt = new SimpleDateFormat("yyyyMMdd").format(date);
                for (long curStepIndex = from; curStepIndex <= to; curStepIndex++) {
                    System.out.printf("producing records with %s-%d...\n", curDt, curStepIndex);

                    int cnt = 0;
                    for (String rid : ridSet) {
                        double speedMock = speedL + random.nextInt(speedU - speedL) + random.nextDouble();
                        double travelTimeMock = travelTimeL + random.nextInt(travelTimeU - travelTimeL) + random.nextDouble();
                        double reliabilityCodeMock = reliabilityCodeL + random.nextInt(reliabilityCodeU - reliabilityCodeL) + random.nextDouble();

                        Row newRow = new Row(7);
                        newRow.setField(0, rid);
                        newRow.setField(1, travelTimeMock);
                        newRow.setField(2, speedMock);
                        newRow.setField(3, reliabilityCodeMock);
                        newRow.setField(4, curStepIndex);
                        newRow.setField(5, date.getDay());
                        newRow.setField(6, date.getTime() + (long) curStepIndex * 60 * 1000);
                        producer.send(new ProducerRecord<>(topic, newRow.toString()));
                        cnt++;
                    }

                    System.out.printf("completed! contains %d records.\n", cnt);
                }
            }
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
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n" +
                            "\t%-20s%s\n",
                    "--servers", "kafka servers to connect, default value is \"kafka-service:9092\".",
                    "--topic", "topic which to import, default value is \"mock_speed_rt\".",
                    "--initial-dt", "default value is \"" + INITIAL_DT + "\".",
                    "--day", "default value is " + DAY + ".",
                    "--from", "lower for step_index, default value is 0.",
                    "--to", "upper from step_index, default value is 1");
            return;
        }
        String servers = parameterTool.get("servers", "kafka-service:9092");
        String topic = parameterTool.get("topic", "mock_speed_rt");
        String initialDt = parameterTool.get("dt-from", INITIAL_DT);
        long day = parameterTool.get("day") == null ? DAY : Long.parseLong(parameterTool.get("day"));
        int from = parameterTool.get("from") == null ? 0 : Integer.parseInt(parameterTool.get("from"));
        int to = parameterTool.get("to") == null ? 1 : Integer.parseInt(parameterTool.get("to"));

        System.out.printf("Usage:\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n" +
                        "\t%-20s%s\n",
                "--servers", servers,
                "--topic", topic,
                "--initial-dt", initialDt,
                "--day", day,
                "--from", from,
                "--to", to);

        // load
        loadDataFromSpeedRT(servers, topic, initialDt, day, from, to);
        System.out.println("complete!");
    }
}
