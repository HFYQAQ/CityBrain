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

public class GenerateSpeedRT4PB {
    private static final Random random = new Random();
    private static final int DAY = 1;
    private static final String DT = "20190327";

    private static final String SPEED_RT_SOURCE = "select rid, travel_time, speed, reliability_code, step_index, WEEKDAY(dt) as day_of_week, UNIX_TIMESTAMP(dt) as timestamp from %s where step_index=?;";
    public static final String JDBC_URL = "jdbc:mysql://mysql-svc:3306/city_brain?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";
    private static final String DESCRIPTOR = "In all the other modes, the application’s main() method is executed on the client side. This process includes downloading the application’s dependencies locally, executing the main() to extract a representation of the application that Flink’s runtime can understand (i.e. the JobGraph) and ship the dependencies and the JobGraph(s) to the cluster. This makes the Client a heavy resource consumer as it may need substantial network bandwidth to download dependencies and ship binaries to the cluster, and CPU cycles to execute the main(). This problem can be more pronounced when the Client is shared across users. Building on this observation, the Application Mode creates a cluster per submitted application, but this time, the main() method of the application is executed by the JobManager. Creating a cluster per application can be seen as creating a session cluster shared only among the jobs of a particular application, and torn down when the application finishes. With this architecture, the Application Mode provides the same resource isolation and load balancing guarantees as the Per-Job mode, but at the granularity of a whole application. The Application Mode builds on an assumption that the user jars are already available on the classpath (usrlib folder) of all Flink components that needs access to it (JobManager, TaskManager). In other words, your application comes bundled with the Flink distribution. This allows the application mode to speed up the deployment / recovery process, by not having to distribute the user jars to the Flink components via RPC as the other deployment modes do. Compared to the Per-Job mode, the Application Mode allows the submission of applications consisting of multiple jobs. The order of job execution is not affected by the deployment mode but by the call used to launch the job. Using execute(), which is blocking, establishes an order and it will lead to the execution of the “next” job being postponed until “this” job finishes.";

    private static void loadDataFromSpeedRT(String servers, String tableName, String topic, int from, int to) {
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
        List<Row> records = new ArrayList<>();
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

                records.add(new Row(15) {
                    {
                        setField(0, statTime);
                        setField(1, rid);
                        setField(2, stepIndex);
                        setField(3, dataStepIndex);
                        setField(4, dataTp);
                        setField(5, dataStepTime);
                        setField(6, speed);
                        setField(7, nostopSpeed);
                        setField(8, travelTime);
                        setField(9, nostopTravelTime);
                        setField(10, reliabilityCode);
                        setField(11, dt);
                        setField(12, tp);
                        setField(13, dataVersion);
                        setField(14, adcode);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write
        try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
            Date dtFrom = new SimpleDateFormat("yyyyMMdd").parse(DT);
            Date dtTo = new Date(dtFrom.getTime() + (DAY - 1) * 24 * 60 * 60 * 1000);
            for (Date date = dtFrom; date.compareTo(dtTo) <= 0; date = new Date(date.getTime() + 24 * 60 * 60 * 1000)) {
                String curDt = new SimpleDateFormat("yyyyMMdd").format(date);
                for (long curStepIndex = from; curStepIndex < to; curStepIndex++) {
                    System.out.printf("producing records with %s-%d...\n", curDt, curStepIndex);

                    int cnt = 0;
                    for (Row row : records) {
                        double speedMock = speedL + random.nextInt(speedU - speedL) + random.nextDouble();
                        double travelTimeMock = travelTimeL + random.nextInt(travelTimeU - travelTimeL) + random.nextDouble();
                        double reliabilityCodeMock = reliabilityCodeL + random.nextInt(reliabilityCodeU - reliabilityCodeL) + random.nextDouble();

                        Row newRow = new Row(8);
                        newRow.setField(0, row.getField(1));
                        newRow.setField(1, travelTimeMock);
                        newRow.setField(2, speedMock);
                        newRow.setField(3, reliabilityCodeMock);
                        newRow.setField(4, curStepIndex);
                        newRow.setField(5, date.getDay());
                        newRow.setField(6, date.getTime() + (long) curStepIndex * 60 * 1000);
                        newRow.setField(7, DESCRIPTOR);
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
                            "\t%-20s%s\n",
                    "--servers", "kafka servers to connect, default value is \"kafka-service:9092\".",
                    "--tableName", "table which to load, default value is \"table2\".",
                    "--topic", "topic which to import, default value is \"mock_speed_rt\".",
                    "--from", "lower for step_index, default value is 0.",
                    "--to", "upper from step_index, default value is 1");
            return;
        }
        String servers = parameterTool.get("servers", "kafka-service:9092");
        String tableName = parameterTool.get("tableName", "table2");
        String topic = parameterTool.get("topic", "mock_speed_rt");
        int from = parameterTool.get("from") == null ? 0 : Integer.parseInt(parameterTool.get("from"));
        int to = parameterTool.get("to") == null ? 1 : Integer.parseInt(parameterTool.get("to"));

        // load
        loadDataFromSpeedRT(servers, tableName, topic, from, to);
        System.out.println("complete!");
    }
}
