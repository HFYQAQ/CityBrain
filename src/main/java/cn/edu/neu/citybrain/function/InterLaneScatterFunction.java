package cn.edu.neu.citybrain.function;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;

public class InterLaneScatterFunction implements FlatMapFunction<Row, Row> {
    @Override
    // [0]tag, [1]rid, [2]travel_time, [3]speed, [4]reliability_code, [5]step_index_1mi, [6]step_index_10mi, [7]day_of_week, [8]timestamp
    // [9]len
    // [10]inter_id, [11]turn_dir_no_list, [12]lane_id
    public void flatMap(Row row, Collector<Row> collector) throws Exception {
        String[] turnDirNoList = ((String) row.getField(11)).split(",");
        for (String turnDirNo : turnDirNoList) {
            Row newRow = new Row(row.getArity());
            newRow.setField(0, row.getField(0)); // tag
            newRow.setField(1, row.getField(10)); // inter_id
            newRow.setField(2, row.getField(1)); // rid
            newRow.setField(3, Long.parseLong(turnDirNo)); // turn_dir_no
            newRow.setField(4, row.getField(7)); // day_of_week
            newRow.setField(5, row.getField(5)); // step_index_1mi
            newRow.setField(6, row.getField(6)); // step_index_10mi
            newRow.setField(7, row.getField(8)); // timestamp

            newRow.setField(8, row.getField(2)); // travel_time
            newRow.setField(9, row.getField(3)); // speed
            newRow.setField(10, row.getField(4)); // reliability_code

            newRow.setField(11, row.getField(9).equals("") ? 0d : Double.parseDouble((String) row.getField(9))); // len

            newRow.setField(12, row.getField(12)); // lane_id
            collector.collect(newRow);
        }
    }

    public static RowTypeInfo getRowTypeInfo() {
        return new RowTypeInfo(
                TypeInformation.of(Integer.TYPE),
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                BasicTypeInfo.STRING_TYPE_INFO);
    }
}
