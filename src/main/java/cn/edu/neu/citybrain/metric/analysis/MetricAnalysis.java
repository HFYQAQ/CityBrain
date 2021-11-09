package cn.edu.neu.citybrain.metric.analysis;

import cn.edu.neu.citybrain.db.DBQuery;
import cn.edu.neu.citybrain.metric.struct.Metric;
import cn.edu.neu.citybrain.metric.struct.Statistic;
import cn.edu.neu.citybrain.util.CityBrainUtil;
import cn.edu.neu.citybrain.util.ParameterTool;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MetricAnalysis {
    private ExecutorService executorService;

    public MetricAnalysis() {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    public static void main(String[] args) throws Exception {
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        if (parameterTool.has("h")) {
            System.out.printf("Usage:\n\t%-20s%s\n",
                    "--jobName", "job name.",
                    "--dt", "date, default value is formatted from postfix of jobName.",
                    "--stepIndex", "step index.");
            return;
        }
        String jobName = parameterTool.get("jobName");

        new MetricAnalysis().analysis(jobName);
    }

    private void analysis(String jobName) {
        String tag = "statistic";
        String sql = "select * from statistic where job_name=?";
        DBQuery dbQuery = new DBQuery(executorService);
        dbQuery.add(
                tag,
                sql,
                Statistic.class,
                new ArrayList<String>() {
                    {
                        add("jobName");
                        add("subtaskIndex");
                        add("dt");
                        add("stepIndex1mi");
                        add("amount");
                        add("duration");
                    }
                },
                jobName);

        dbQuery.execute();

        Map<String, List<Statistic>> statisticMap = dbQuery.<Statistic>get(tag).stream().collect(Collectors.groupingBy(Statistic::getKeyJobDtIndex));
        List<Metric> metrics = new ArrayList<>();
        for (Map.Entry<String, List<Statistic>> entry : statisticMap.entrySet()) {
            String keyJobDtIndex = entry.getKey();
            List<Statistic> list = entry.getValue();

            long totalAmount = 0;
            long totalDuration = 0;
            for (Statistic statistic : list) {
                totalAmount += statistic.getAmount();
                totalDuration += statistic.getDuration();
            }
            double throughput = totalAmount * 1.0 / totalDuration * 1000;
            double delay = totalDuration * 1.0 / totalAmount;
            String[] splits = CityBrainUtil.split(keyJobDtIndex);

            if (splits.length == 3) {
                Metric metric = new Metric(splits[0], splits[1], Long.parseLong(splits[2]), throughput, delay);
                metrics.add(metric);
            }
        }

        System.out.println(Arrays.toString(metrics.toArray()));
    }

    private static String formatJobName(String jobName) {
        long timestamp = Long.parseLong(jobName.substring(jobName.indexOf("_") + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(timestamp);
    }
}
