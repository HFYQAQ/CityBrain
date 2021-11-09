package cn.edu.neu.citybrain.function.sink;

import cn.edu.neu.citybrain.dto.my.RoadMetric;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.util.List;

public class MetricSinkFunction extends RichSinkFunction<List<RoadMetric>> {
    //metric
    private static long INTERVAL = 3 * 1000;
    private long begin = System.currentTimeMillis();
    private long cnt = 0;
    private double throughput = 0;
    private double delay = 0;
    private double totalThroughput = 0;
    private double totalDelay = 0;
    private double stCnt = 0;

    public MetricSinkFunction() {

    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
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
        cnt++;
    }

    @Override
    public void close() throws Exception {
        super.close();

        double avgThroughput = totalThroughput / stCnt;
        double avgDelay = totalDelay / stCnt;
        System.out.println("[flink] avg_throughput: " + avgThroughput + "/s     " + "avg_delay: " + avgDelay + "ms");
    }
}
