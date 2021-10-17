package cn.edu.neu.citybrain.connector.kafka.util;

import cn.edu.neu.citybrain.util.CityBrainUtil;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class CSV2Kafka {
    private static void loadDataFromSpeedRT(String servers, String csvFile, int from, int to) { // 89735条数据，只有883一个时间片
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("linger.ms", 1);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        File file = new File(csvFile);
        try (Producer<String, String> producer = new KafkaProducer<>(properties);
             FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) { // dt=20210327, step_index=883
            br.readLine(); // 跳过列名行
            br.mark((int) file.length() + 1);

            System.out.printf("load %s with step_index within [%d, %d].\n", csvFile, from, to);
            for (int stepIndex = from; stepIndex <= to; stepIndex++) {
                br.reset();
                int cnt = 0;

                String line;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.substring(1, line.length() - 1).split("\",\""); // 共16列
                    if (fields.length != 16) {
                        throw new Exception("invalid csv format.");
                    }
                    Row row = new Row(7);
                    row.setField(0, fields[2]); // rid
                    row.setField(1, fields[9]); // travel_time
                    row.setField(2, fields[7]); // speed
                    row.setField(3, fields[11]); // reliability_code
                    row.setField(4, stepIndex); // stepIndex
                    row.setField(5, CityBrainUtil.weekday(fields[12])); // day_of_week(dt)
                    row.setField(6, CityBrainUtil.unixTimestamp(fields[12]) + (long) stepIndex * 60 * 1000); // timestamp(dt)

                    producer.send(new ProducerRecord<>(Constants.TOPIC_DWS_TFC_STATE_RID_TP_LASTSPEED_RT, row.toString()));
                    cnt++;
                }

                System.out.printf("importing step_index %d, contains %d records.\n", stepIndex, cnt);
                br.reset();
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
                            "\t%-20s%s\n",
                    "--servers", "kafka servers to connect.",
                    "--csvFile", "csv file which to load, must be specified explicitly.",
                    "--from", "lower for step_index, default value is 0.",
                    "--to", "upper from step_index, default value is 1");
            return;
        }
        if (!parameterTool.has("servers")) {
            throw new Exception("kafka servers which to connect must be specified explicitly!");
        }
        if (!parameterTool.has("csvFile")) {
            throw new Exception("csv file which to load must be specified explicitly!");
        }
        String servers = parameterTool.get("servers");
        String csvFile = parameterTool.get("csvFile");
        int from = parameterTool.get("from") == null ? 0 : Integer.parseInt(parameterTool.get("from"));
        int to = parameterTool.get("to") == null ? 1 : Integer.parseInt(parameterTool.get("to"));

        loadDataFromSpeedRT(servers, csvFile, from, to);
        System.out.println("complete!");
    }
}
