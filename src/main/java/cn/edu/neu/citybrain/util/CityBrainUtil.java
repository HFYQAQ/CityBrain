package cn.edu.neu.citybrain.util;

import org.apache.flink.runtime.state.KeyGroupRangeAssignment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CityBrainUtil {
    public static int[] generateKeyTagForPartition(int parallelism, int maxParallelism) {
        int[] res = new int[parallelism];
        Set<Integer> set = new HashSet<>();
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < parallelism; i++) {
            int key = random.nextInt(maxParallelism * 100);
            int subTaskIdx;
            while (set.contains(subTaskIdx = KeyGroupRangeAssignment.assignKeyToParallelOperator(key, maxParallelism, parallelism))) {
                key = random.nextInt(maxParallelism * 100);
            }
            set.add(subTaskIdx);
            res[i] = key;
//            System.out.printf("%d -> %d\n", key, subTaskIdx);
        }

        StringBuilder sb = new StringBuilder("preallocated keys(key->subTaskIdx): ");
        for (int i = 0; i < res.length; i++) {
            sb.append(res[i]).append("->").append(KeyGroupRangeAssignment.assignKeyToParallelOperator(res[i], maxParallelism, parallelism)).append(", ");
        }
        String out = sb.toString();
        System.out.println(out.substring(0, out.length() - 2));
        return res;
    }

    public static String concat(Object... args) {
        return Arrays.asList(args).stream().map(op -> op.toString())
                .collect(Collectors.joining(SignalOptConstant.Seperator));
    }

    public static String[] split(String str) {
        return str.split(SignalOptConstant.Seperator);
    }

    public static int weekday(String dt) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Date date = sdf.parse(dt);
        return date.getDay();
    }

    public static long unixTimestamp(String dt) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Date date = sdf.parse(dt);
        return date.getTime();
    }

    public static void main(String[] args) throws ParseException {
        String dt = "20210913";
        System.out.println(weekday(dt) + ", " + unixTimestamp(dt));
    }
}
