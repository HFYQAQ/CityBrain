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
    private String servers;
    private String topic;
    private Producer<String, String> producer;

    public KafkaSinkFunction(String servers, String topic) {
        this.servers = servers;
        this.topic = topic;
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
        System.out.println("[HFYLOG] receive " + value.size());
        for (RoadMetric roadMetric : value) {
            producer.send(new ProducerRecord<>(topic, roadMetric.toString()));
        }
        System.out.println("[HFYLOG] produce to kafka.");
    }

    @Override
    public void close() throws Exception {
        super.close();

        producer.close();
    }
}
