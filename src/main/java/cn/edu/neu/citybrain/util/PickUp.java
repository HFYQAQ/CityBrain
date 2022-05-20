package cn.edu.neu.citybrain.util;

import cn.edu.neu.citybrain.db.DBConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PickUp {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/hangzhou?characterEncoding=UTF-8&useSSL=false&serverTimezone=Hongkong&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";

    private static final String READ_SQL = "select * from dws_tfc_state_rid_tpwkd_index_m where day_of_week=? and step_index=?";
    private static final String WRITE_SQL = "insert into dws_tfc_state_rid_tpwkd_index_m_1(`stat_month`,`rid`,`day_of_week`,`step_index`,`avg_speed_1m`,`avg_nostop_speed_1m`,`avg_travel_time_1m`,`avg_nostop_travel_time_1m`,`med_speed_1m`,`med_nostop_speed_1m`,`med_travel_time_1m`,`med_nostop_travel_time_1m`,`avg_speed_3m`,`avg_nostop_speed_3m`,`avg_travel_time_3m`,`avg_nostop_travel_time_3m`,`med_speed_3m`,`med_nostop_speed_3m`,`med_travel_time_3m`,`med_nostop_travel_time_3m`,`month`,`tp`,`data_version`,`adcode`) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final int DAY_OF_WEEK = 3;

    public static void main(String[] args) throws Exception {
        try (Connection connection = DriverManager.getConnection(URL, DBConstants.JDBC_USER, DBConstants.JDBC_PWD);
             PreparedStatement psRead = connection.prepareStatement(READ_SQL);
             PreparedStatement psWrite = connection.prepareStatement(WRITE_SQL)) {
            connection.setAutoCommit(false);

            for (int i = 0; i <= 719; i++) {
                psRead.clearParameters();
                psRead.setObject(1, DAY_OF_WEEK);
                psRead.setObject(2, i);

                ResultSet resultSet = psRead.executeQuery();
                System.out.printf("query results with day_of_week %d and step_index %d have returned.\n", DAY_OF_WEEK, i);
                int cnt = 0;
                while (resultSet.next()) {
                    psWrite.clearParameters();
                    psWrite.setObject(1, resultSet.getObject(2)); // 忽略id，从2开始
                    psWrite.setObject(2, resultSet.getObject(3));
                    psWrite.setObject(3, resultSet.getObject(4));
                    psWrite.setObject(4, resultSet.getObject(5));
                    psWrite.setObject(5, resultSet.getObject(6));
                    psWrite.setObject(6, resultSet.getObject(7));
                    psWrite.setObject(7, resultSet.getObject(8));
                    psWrite.setObject(8, resultSet.getObject(9));
                    psWrite.setObject(9, resultSet.getObject(10));
                    psWrite.setObject(10, resultSet.getObject(11));
                    psWrite.setObject(11, resultSet.getObject(12));
                    psWrite.setObject(12, resultSet.getObject(13));
                    psWrite.setObject(13, resultSet.getObject(14));
                    psWrite.setObject(14, resultSet.getObject(15));
                    psWrite.setObject(15, resultSet.getObject(16));
                    psWrite.setObject(16, resultSet.getObject(17));
                    psWrite.setObject(17, resultSet.getObject(18));
                    psWrite.setObject(18, resultSet.getObject(19));
                    psWrite.setObject(19, resultSet.getObject(20));
                    psWrite.setObject(20, resultSet.getObject(21));
                    psWrite.setObject(21, resultSet.getObject(22));
                    psWrite.setObject(22, resultSet.getObject(23));
                    psWrite.setObject(23, resultSet.getObject(24));
                    psWrite.setObject(24, resultSet.getObject(25));
                    psWrite.addBatch();

                    cnt++;
                }
                psWrite.executeBatch();
                System.out.printf("records with day_of_week %d and step_index %d have been inserted. (total num: %d)\n", DAY_OF_WEEK, i, cnt);
            }
        }
        System.out.println("completed!");
    }
}
