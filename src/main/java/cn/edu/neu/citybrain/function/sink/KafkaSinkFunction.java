package cn.edu.neu.citybrain.function.sink;

import cn.edu.neu.citybrain.dto.my.RoadMetric;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.List;
import java.util.Properties;

public class KafkaSinkFunction extends RichSinkFunction<List<RoadMetric>> {
    private static final String OUT_TOPIC = "citybrain_out";
    private String servers;
    private Producer<String, String> producer;

    //metric
    private static long INTERVAL = 3 * 1000;
    private long begin = System.currentTimeMillis();
    private long cnt = 0;
    private double throughput = 0;
    private double delay = 0;
    private double totalThroughput = 0;
    private double totalDelay = 0;
    private double stCnt = 0;

    public KafkaSinkFunction(String servers) {
        this.servers = servers;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);
        properties.put("acks", "0");
        properties.put("retries", 0);
        properties.put("linger.ms", 1);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(properties);
    }

    @Override
    public void invoke(List<RoadMetric> value, Context context) throws Exception {
        if (cnt != 0 && System.currentTimeMillis() - begin >= INTERVAL) {
            throughput = cnt * 1.0 / INTERVAL * 1000;
            delay = INTERVAL * 1.0 / cnt;
            System.out.println("hfy: " + cnt + ", " + throughput + "/s, " + delay + "ms.");
            if (stCnt != 0) { // 第一次不统计
                totalThroughput += throughput;
                totalDelay += delay;
            }
            stCnt++;

            cnt = 0;
            begin = System.currentTimeMillis();
        }

        for (RoadMetric roadMetric : value) {
            producer.send(new ProducerRecord<>(OUT_TOPIC, roadMetric.toString()));
        }
        cnt++;
    }

    @Override
    public void close() throws Exception {
        super.close();

        producer.close();

        double avgThroughput = totalThroughput / (stCnt - 1);
        double avgDelay = totalDelay / (stCnt - 1);
        System.out.println("[flink] avg_throughput: " + avgThroughput + "/s     " + "avg_delay: " + avgDelay + "ms");
    }
}
