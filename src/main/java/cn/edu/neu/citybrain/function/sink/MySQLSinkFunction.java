package cn.edu.neu.citybrain.function.sink;

import cn.edu.neu.citybrain.db.DBConnection;
import cn.edu.neu.citybrain.dto.my.RoadMetric;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class MySQLSinkFunction extends RichSinkFunction<List<RoadMetric>> {
    // id, inter_id, f_rid, turn_dir_no, dt, step_index, travel_time, delay_dur, stop_cnt, queue_len
//    private final String SQL = "insert into statistic(job_name,subtask_index,dt,step_index_1mi,amount,duration) values(?,?,?,?,?,?)";
    private final String SQL = "insert into inter_metric_v2(inter_id, f_rid, turn_dir_no, dt, step_index, travel_time, delay_dur, stop_cnt, queue_len) values(?,?,?,?,?,?,?,?,?)";
    private Connection connection;
    private PreparedStatement ps;

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        connection = DBConnection.getConnection();
        ps = connection.prepareStatement(SQL);
    }

    @Override
    public void invoke(List<RoadMetric> value, Context context) throws Exception {
        for (RoadMetric roadMetric : value) {
            ps.clearParameters();

            // position
            String interId = roadMetric.getInterId();
            String fRid = roadMetric.getfRid();
            Long turnDirNo = roadMetric.getTurnDirNo();
            // time
            String dt = roadMetric.getDt();
            Long stepIndex = roadMetric.getStepIndex1mi();
            // metric
            Double travelTime = roadMetric.getTravelTime();
            Double delayDur = roadMetric.getDelay();
            Double stopCnt = roadMetric.getStopCnt();
            Double queueLen = roadMetric.getQueue();

            ps.setObject(1, interId);
            ps.setObject(2, fRid);
            ps.setObject(3, turnDirNo);
            ps.setObject(4, dt);
            ps.setObject(5, stepIndex);
            ps.setObject(6, travelTime);
            ps.setObject(7, delayDur);
            ps.setObject(8, stopCnt);
            ps.setObject(9, queueLen);
            ps.executeUpdate();
        }
    }

    @Override
    public void close() throws Exception {
        super.close();

        if (ps != null) {
            ps.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
