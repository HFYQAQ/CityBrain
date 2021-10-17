package cn.edu.neu.citybrain.util;

import java.util.HashMap;
import java.util.Map;

public class ParameterTool {
    private Map<String, String> map;

    public static ParameterTool fromArgs(String[] args) throws Exception {
        ParameterTool parameterTool = new ParameterTool();
        parameterTool.map = new HashMap<>(args.length >> 1);

        if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--h"))) {
            parameterTool.map.put("h", null);
            return parameterTool;
        }

        String k = "", v = "";
        for (int i = 0; i < args.length; i++) {
            if ((i & 0x1) == 0) {
                if (!args[i].startsWith("--") && !args[i].startsWith("-"))
                    throw new Exception("Invalid parameters.");
                if (args[i].startsWith("-")) {
                    k = args[i].substring(1);
                }
                if (args[i].startsWith("--")) {
                    k = args[i].substring(2);
                }
            } else {
                v = args[i];
                parameterTool.map.put(k, v);
            }
        }
        return parameterTool;
    }

    public String get(String k) {
        return map.get(k);
    }

    public boolean has(String k) {
        return map.containsKey(k);
    }
}

