package cn.edu.neu.citybrain.metric.analysis;

import cn.edu.neu.citybrain.util.ParameterTool;

import java.text.SimpleDateFormat;

public class MetricAnalysis {
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
        String dt = parameterTool.get("dt") != null ? parameterTool.get("dt") : formatJobName(jobName);
        long stepIndex = Long.parseLong(parameterTool.get("stepIndex"));

        System.out.println(dt);
//        new MetricAnalysis().analysis();
    }

    private static String formatJobName(String jobName) {
        long timestamp = Long.parseLong(jobName.substring(jobName.indexOf("_") + 1));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(timestamp);
    }
}
