package cn.edu.neu.citybrain.function;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;

public class DefaultValueForNdIndexFunction extends RichMapFunction<Row, Row> {
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public Row map(Row row) throws Exception {
        row.setField(14, ((String) row.getField(14)).equals("") ? "0" : row.getField(14));
        return row;
    }

    @Override
    public void close() throws Exception {
        super.close();
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
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO,
                BasicTypeInfo.STRING_TYPE_INFO);
    }
}
