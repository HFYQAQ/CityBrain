package cn.edu.neu.citybrain.function.source;

import cn.edu.neu.citybrain.db.DBConstants;
import cn.edu.neu.citybrain.db.DBConnection;
import cn.edu.neu.citybrain.util.CityBrainUtil;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.types.Row;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpeedRTSourceFunction extends RichSourceFunction<Row> {
    private transient Connection conn;
    private transient PreparedStatement statement;
    private int curStepIndex = 883;
    private long delay; // ms
    private int parallelism;
    private int maxParallelism;
    private static int autoInc; // 对parallelism取模为每条数据打上key，实现rebalance，保证同一个子任务上的数据共用同一个key
    private int[] preallocatedKeys;

    public SpeedRTSourceFunction(long delay, int parallelism, int maxParallelism) {
        this.delay = delay;
        this.parallelism = parallelism;
        this.maxParallelism = maxParallelism;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        preallocatedKeys = CityBrainUtil.generateKeyTagForPartition(parallelism, maxParallelism);

        if (this.conn == null) {
            this.conn = DBConnection.getConnection();
        }
        if (this.conn == null) {
            throw new NullPointerException();
        }
        if (this.statement == null) {
            try {
                this.statement = this.conn.prepareStatement(DBConstants.SPEED_RT_SOURCE);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            if (this.statement == null) {
                throw new NullPointerException();
            }
        }
    }

    @Override
    public void run(SourceContext<Row> sourceContext) throws Exception {
        int autoIncStepIndex = 0; // 每轮读取的都是相同数据，所以为了驱动上一个窗口，时间片递增。

        for (int i = 0; i < 7; i++) {
            Thread.sleep(5 * 1000);
            System.out.printf("sleeping %ds.\n", (i + 1) * 5);
        }
        System.out.println("unblock stream");

        for (int i = 0; i < 2; i++) {
            try {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Row ret = new Row(9);
                    autoInc = autoInc == Integer.MAX_VALUE ? 0 : autoInc;
                    ret.setField(0, preallocatedKeys[autoInc++ % parallelism]); // attach tag as partition key
                    ret.setField(1, resultSet.getString("rid"));
                    ret.setField(2, resultSet.getDouble("travel_time"));
                    ret.setField(3, resultSet.getDouble("speed"));
                    ret.setField(4, resultSet.getDouble("reliability_code"));
                    long step_index_1mi = (long) resultSet.getInt("step_index") + autoIncStepIndex;
                    ret.setField(5, step_index_1mi);
                    long step_index_10mi = step_index_1mi / 10;
                    ret.setField(6, step_index_10mi); // convert from 1mi to 10mi
                    ret.setField(7, (long) resultSet.getInt("day_of_week"));
                    ret.setField(8, resultSet.getLong("timestamp") * 1000 + step_index_1mi * 60 * 1000); // 以1mi的时间片长度计算
                    sourceContext.collect(ret);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            autoIncStepIndex++;

            // delay
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws Exception {
        super.close();

        if (this.statement != null) {
            this.statement.close();
        }
        if (this.conn != null) {
            this.conn.close();
        }
    }

    @Override
    public void cancel() {

    }

    public static RowTypeInfo getRowTypeInfo() {
        return new RowTypeInfo(
                TypeInformation.of(Integer.TYPE),
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Double.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE),
                TypeInformation.of(Long.TYPE));
    }
}
